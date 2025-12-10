/* =====================================================
   dadam.quiz.js
   - 신조어 퀴즈: 오늘자 1개 조회 + 보기별 투표 + 정답 확인
   - 백엔드:
       GET  /api/v1/quiz/today      → SlangQuizTodayResponse
       POST /api/v1/quiz/today/vote → SlangQuizTodayResponse
===================================================== */

/* ================= 공통 상수 ================= */
const QUIZ_TODAY_API_URL = "/api/v1/quiz/today";
const QUIZ_VOTE_API_URL  = "/api/v1/quiz/today/vote";

const quizContainer   = document.getElementById("slang-quiz");
const quizQuestionEl  = document.getElementById("quiz-question");
const quizOptionsList = document.getElementById("quiz-options");
const quizFeedbackEl  = document.getElementById("quiz-feedback");
const quizCheckBtn    = document.getElementById("quiz-submit-btn");

let currentQuiz   = null;
let selectedIndex = null;  // 내가 현재 화면에서 고른 보기 인덱스
let revealed      = false; // 정답 확인 상태 여부

/* ---------------- 아바타 라벨 헬퍼 ---------------- */
function getQuizAvatarLabel(rawName) {
    if (!rawName) return "가족";
    const name = String(rawName).trim();
    if (name.length === 0) return "가족";
    const parts = name.split(/\s+/);
    const lastPart = parts[parts.length - 1];

    if (/^[가-힣]+$/.test(lastPart)) {
        if (lastPart.length <= 2) return lastPart;
        if (lastPart.length === 3) return lastPart.slice(1);
        return lastPart;
    }
    return lastPart;
}

/* ---------------- 응답 정규화 ---------------- */
function normalizeQuizSummary(raw) {
    if (!raw) return null;

    const choices = Array.isArray(raw.choices) ? raw.choices : [];

    const votes0 = Array.isArray(raw.votes0) ? raw.votes0 : [];
    const votes1 = Array.isArray(raw.votes1) ? raw.votes1 : [];
    const votes2 = Array.isArray(raw.votes2) ? raw.votes2 : [];

    const answerText  = raw.answer || "";
    const answerIndex = typeof raw.answerIndex === "number"
        ? raw.answerIndex
        : -1;

    const myChoiceIndex = typeof raw.myChoiceIndex === "number"
        ? raw.myChoiceIndex
        : null;

    return {
        id: raw.id,
        question: raw.question || "신조어 퀴즈",
        choices,
        explanation: raw.explanation || "",
        answer: answerText,
        answerIndex,
        myChoiceIndex,
        votes: [votes0, votes1, votes2],
    };
}

/* ---------------- 렌더링 ---------------- */
function renderQuiz() {
    if (!quizContainer || !currentQuiz) return;

    if (quizQuestionEl) {
        quizQuestionEl.textContent = currentQuiz.question;
    }

    if (quizOptionsList) {
        quizOptionsList.innerHTML = currentQuiz.choices
            .map((opt, idx) => {
                return `
          <li class="quiz-option-item">
            <button class="quiz-option" type="button" data-index="${idx}">
              <span class="quiz-option-label">${idx + 1}.</span>
              <span class="quiz-option-text">${opt}</span>
            </button>
            <div class="quiz-option-meta">
              <div class="quiz-option-bar" data-quiz-bar="${idx}"></div>
              <span class="quiz-option-percent" data-quiz-percent="${idx}">0%</span>
              <div class="quiz-option-avatars" data-quiz-avatars="${idx}"></div>
            </div>
          </li>
        `;
            })
            .join("");
    }

    if (quizFeedbackEl) {
        // 기본 상태에서는 비워두고, revealed 상태에서 다시 채움
        quizFeedbackEl.textContent = "";
    }

    updateQuizVisuals();
}

/* 보기별 퍼센트, 버튼 상태 등 */
function updateQuizVisuals() {
    if (!currentQuiz) return;

    // 서버에서 내려준 내 선택 (이미 투표한 경우)
    const lockedIndex = currentQuiz.myChoiceIndex;
    const isLocked    = lockedIndex !== null && lockedIndex !== undefined;

    // selectedIndex가 없다면 서버 값으로 채워 줌
    if (isLocked && selectedIndex === null) {
        selectedIndex = lockedIndex;
    }

    const totalVotes =
        (currentQuiz.votes[0]?.length || 0) +
        (currentQuiz.votes[1]?.length || 0) +
        (currentQuiz.votes[2]?.length || 0);

    currentQuiz.choices.forEach((_, idx) => {
        const bar         = document.querySelector(`[data-quiz-bar="${idx}"]`);
        const percentSpan = document.querySelector(`[data-quiz-percent="${idx}"]`);
        const avatarsBox  = document.querySelector(`[data-quiz-avatars="${idx}"]`);
        const optionBtn   = quizOptionsList?.querySelector(
            `.quiz-option[data-index="${idx}"]`
        );

        const votesForChoice = currentQuiz.votes[idx] || [];
        const percent =
            totalVotes === 0
                ? 0
                : Math.round((votesForChoice.length / totalVotes) * 100);

        if (bar) bar.style.width = percent + "%";
        if (percentSpan) percentSpan.textContent = percent + "%";

        if (avatarsBox) {
            avatarsBox.innerHTML = votesForChoice
                .map((voter) => {
                    const rawName =
                        typeof voter === "string"
                            ? voter
                            : (voter.userName || "가족");
                    const label = getQuizAvatarLabel(rawName);
                    return `
              <span class="avatar avatar-sm">
                <span class="avatar-initial">${label}</span>
              </span>
            `;
                })
                .join("");
        }

        if (optionBtn) {
            optionBtn.classList.remove("selected", "correct", "wrong");

            // 현재 화면에서 사용자가 선택한 보기
            if (selectedIndex === idx) {
                optionBtn.classList.add("selected");
            }

            // 정답 확인 이후에는 정답/오답 색깔 표시
            if (revealed && currentQuiz.answerIndex !== -1) {
                if (idx === currentQuiz.answerIndex) {
                    optionBtn.classList.add("correct");
                } else if (idx === selectedIndex) {
                    optionBtn.classList.add("wrong");
                }
            }

            // ❌ 이 줄 때문에 disabled가 되어 클릭 이벤트가 막혔었음
            // optionBtn.disabled = revealed && isLocked;

            // ✅ 항상 클릭 가능하게 둔다 (로직은 클릭 핸들러에서 제어)
            optionBtn.disabled = false;
        }
    });

    // 정답 확인 버튼: 선택이 있을 때만 보이기
    if (quizCheckBtn) {
        if (selectedIndex === null) {
            quizCheckBtn.style.display = "none";
        } else {
            quizCheckBtn.style.display = "inline-flex";
            quizCheckBtn.disabled = revealed;
        }
    }
}

/* 정답 풀이 텍스트 업데이트 */
function updateQuizFeedback() {
    if (!currentQuiz || !quizFeedbackEl) return;
    if (!revealed || selectedIndex === null) {
        quizFeedbackEl.textContent = "";
        return;
    }

    const isCorrect =
        currentQuiz.answerIndex !== -1 &&
        selectedIndex === currentQuiz.answerIndex;

    if (isCorrect) {
        quizFeedbackEl.textContent =
            "정답이에요! ✨ " + (currentQuiz.explanation || "");
    } else {
        const correctText =
            currentQuiz.answerIndex !== -1
                ? currentQuiz.choices[currentQuiz.answerIndex]
                : currentQuiz.answer;

        quizFeedbackEl.textContent =
            "아惜! 정답은 '" +
            correctText +
            "' 이에요. " +
            (currentQuiz.explanation || "");
    }
}

/* ---------------- 서버에서 오늘 퀴즈 가져오기 (JWT 포함) ---------------- */
async function fetchTodayQuiz() {
    try {
        // apiGet은 Authorization 헤더를 자동으로 붙여줌 (dadam.answers.js)
        const raw = await apiGet(QUIZ_TODAY_API_URL);
        console.log("[QUIZ] today response:", raw);

        const summary = normalizeQuizSummary(raw);
        if (!summary) throw new Error("Invalid quiz data");

        currentQuiz   = summary;
        selectedIndex = summary.myChoiceIndex ?? null;

        // 이미 서버가 "오늘 이 유저가 고른 보기"를 알고 있으면 (myChoiceIndex != null),
        // 새로고침/재방문 시 바로 정답 화면
        if (selectedIndex !== null &&
            selectedIndex !== undefined &&
            summary.answerIndex !== -1) {
            revealed = true;
        } else {
            revealed = false;
        }

        // 기본 렌더링
        renderQuiz();

        // 정답 상태라면 정답/풀이도 바로 채움
        if (revealed) {
            updateQuizFeedback();
            updateQuizVisuals();
        }

        if (typeof addNotification === "function") {
            addNotification({
                type: "info",
                message: "오늘의 신조어 퀴즈가 준비되었어요.",
            });
        }
    } catch (err) {
        console.error("[QUIZ] error:", err);
        if (quizQuestionEl) {
            quizQuestionEl.textContent = "퀴즈를 불러오지 못했어요.";
        }
    }
}

/* ---------------- 서버에 투표 보내기 ---------------- */
async function sendQuizVote(choiceIndex) {
    try {
        const raw = await apiPost(QUIZ_VOTE_API_URL, { choiceIndex });
        console.log("[QUIZ] vote response:", raw);

        const summary = normalizeQuizSummary(raw);
        if (!summary) throw new Error("Invalid quiz vote data");

        currentQuiz   = summary;
        // 서버가 내려준 myChoiceIndex가 최종 투표 결과
        selectedIndex = summary.myChoiceIndex ?? choiceIndex;

        // 투표된 이후에는 정답화면을 보여주기 위해 revealed = true
        revealed = true;

        // 투표 결과(퍼센트, 아바타 등) + 정답 표시 업데이트
        updateQuizFeedback();
        updateQuizVisuals();

        if (typeof addNotification === "function") {
            addNotification({
                type: "info",
                message: `신조어 퀴즈에서 ${selectedIndex + 1}번을 선택했어요.`,
            });
        }
    } catch (err) {
        console.error("[QUIZ] vote error:", err);

        const msg = String(err.message || "");

        if (msg.includes("401")) {
            alert("로그인이 필요해요. 먼저 로그인해 주세요.");
        } else if (msg.includes("이미") || msg.includes("ALREADY_PARTICIPATED")) {
            alert("이미 오늘 퀴즈에 참여하셨어요.");
            // 서버 상태로 다시 동기화
            fetchTodayQuiz();
        } else {
            if (typeof addNotification === "function") {
                addNotification({
                    type: "error",
                    message: "퀴즈 선택에 실패했어요. 잠시 후 다시 시도해 주세요.",
                });
            }
        }
    }
}

/* ---------------- 초기화 & 이벤트 ---------------- */
function initQuiz() {
    if (!quizContainer) return;

    // 처음엔 버튼 숨김
    if (quizCheckBtn) {
        quizCheckBtn.style.display = "none";
    }

    fetchTodayQuiz();
}

/* 보기 버튼 클릭 */
document.addEventListener("click", (e) => {
    const btn = e.target.closest(".quiz-option");
    if (!btn || !quizContainer || !currentQuiz) return;

    const idx = Number(btn.dataset.index);
    if (Number.isNaN(idx)) return;

    const hasLocked =
        currentQuiz.myChoiceIndex !== null &&
        currentQuiz.myChoiceIndex !== undefined;

    // ✅ 이미 오늘 퀴즈에 참여한 상태라면: 어떤 보기든 클릭 시 알림
    if (hasLocked) {
        alert("이미 오늘 퀴즈에 참여하셨어요.");
        return;
    }

    // 아직 정답 공개 전일 때만 선택 가능
    if (revealed) return;

    // 서버에 바로 투표하지 않고, 화면에서 선택만 변경
    selectedIndex = idx;
    updateQuizVisuals();
});

/* "정답 확인" 버튼 */
quizCheckBtn?.addEventListener("click", async () => {
    if (!currentQuiz || selectedIndex === null || revealed) return;

    // 아직 서버에 투표가 안 된 경우에만 투표 요청
    if (currentQuiz.myChoiceIndex === null ||
        currentQuiz.myChoiceIndex === undefined) {
        await sendQuizVote(selectedIndex);
        // sendQuizVote에서 currentQuiz, selectedIndex, revealed를 최신으로 갱신함
        return;
    }

    // 안전망 (실제로는 거의 들어올 일 없음)
    revealed = true;
    updateQuizFeedback();
    updateQuizVisuals();
});

/* DOM 로드 시 초기화 */
document.addEventListener("DOMContentLoaded", () => {
    initQuiz();
});
