/* =====================================================
   dadam.core.js
   - 유저 정보 / 공통 상수
   - 로컬스토리지 관리
   - 인증 토큰 관리
   - 알림(Notification) 시스템
   - 모달 시스템 (열기/닫기 + ESC)
===================================================== */

/* 공통 API 기본 경로 (전역으로 한 번만 선언) */
const API_BASE = "/api/v1";

/* -----------------------------------------------------
   📌 공통 상수 & 로컬 저장 키
----------------------------------------------------- */

const DADAM_KEYS = {
    USER_PROFILE: "dadam_user_profile",
    NOTIFICATIONS: "dadam_notifications",
    ANSWERS: "dadam_answers",
    COMMENTS: "dadam_comments",
    BALANCE_GAME: "dadam_balance_game",
    QUIZ_STATE: "dadam_quiz_state",
    AUTH_TOKEN: "dadam_auth_token", // 🔐 로그인 토큰 저장용
    EVENTS: "dadam_events",
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => document.querySelectorAll(selector);

/* -----------------------------------------------------
   👤 아바타 라벨 헬퍼 (이름 → "수진", "엄마" 등)
----------------------------------------------------- */
function getAvatarLabel(rawName) {
    if (!rawName) return "가족";
    const name = String(rawName).trim();
    if (!name) return "가족";

    // 공백 기준으로 마지막 토큰 사용 (예: "홍 길동" -> "길동")
    const parts = name.split(/\s+/);
    const last = parts[parts.length - 1];

    // 한글 이름 처리
    if (/^[가-힣]+$/.test(last)) {
        if (last.length <= 2) return last;        // "엄마", "아빠", "수진"
        if (last.length === 3) return last.slice(1); // "윤수진" -> "수진"
        return last;                               // 4글자 이상은 그대로
    }

    // 그 외 (영문 등)
    if (last.length <= 3) return last;
    return last.slice(0, 3);
}

/* -----------------------------------------------------
   👤 아바타 공통 데이터 & HTML 빌더
----------------------------------------------------- */

/**
 * userId / userName / avatarUrl 를 바탕으로
 * 최종 표시할 name + avatarUrl 결정
 */
function getAvatarData(userId, userName, explicitAvatarUrl) {
    // 1) 명시적으로 avatarUrl이 넘어온 경우 우선 사용
    if (explicitAvatarUrl) {
        return {
            name: userName || "가족",
            avatarUrl: explicitAvatarUrl,
        };
    }

    // 2) 현재 로그인 유저와 매칭되는 경우
    if (typeof currentUser !== "undefined" && currentUser) {
        if (
            currentUser.id != null &&
            userId != null &&
            String(currentUser.id) === String(userId)
        ) {
            return {
                name: currentUser.name || userName || "나",
                avatarUrl: currentUser.avatarUrl || null,
            };
        }
    }

    // 3) DADAM_FAMILY에 등록된 가족인 경우
    if (
        typeof DADAM_FAMILY !== "undefined" &&
        DADAM_FAMILY &&
        userId &&
        DADAM_FAMILY[userId]
    ) {
        const fam = DADAM_FAMILY[userId];
        return {
            name: fam.name || userName || "가족",
            avatarUrl: fam.avatarUrl || null,
        };
    }

    // 4) 그 외: 이름만 사용, 사진은 없음
    return {
        name: userName || "가족",
        avatarUrl: null,
    };
}

/**
 * 어떤 화면이든 쓸 수 있는 공통 아바타 HTML 생성기
 * - size: "sm" | "md" | "lg"
 * - variant: "default" | "soft" | "accent"
 */
function buildAvatarHtml({
                             userId = null,
                             userName = "",
                             avatarUrl = null,
                             size = "sm",
                             variant = "default",
                         } = {}) {
    const { name, avatarUrl: resolvedUrl } = getAvatarData(
        userId,
        userName,
        avatarUrl
    );
    const label = getAvatarLabel(name);

    const classes = ["avatar", `avatar-${size}`];
    if (variant === "soft") classes.push("avatar-soft");
    if (variant === "accent") classes.push("avatar-accent");

    const style = resolvedUrl
        ? ` style="background-image:url('${resolvedUrl}');background-size:cover;background-position:center;"`
        : "";

    const initial = resolvedUrl ? "" : label;

    return `
      <span class="${classes.join(" ")}"${style}>
        <span class="avatar-initial">${initial}</span>
      </span>
    `;
}


/* -----------------------------------------------------
   👤 기본 유저 정보 (처음 접속 시 자동 생성)
----------------------------------------------------- */

function loadUserProfile() {
    const raw = localStorage.getItem(DADAM_KEYS.USER_PROFILE);
    if (raw) {
        try {
            return JSON.parse(raw);
        } catch (_) {}
    }

    const defaultProfile = {
        id: null,
        name: "우리 가족",
        avatarUrl: null,
        role: "child",
        familyRole: "child",
        familyCode: "",
        email: "",
    };

    localStorage.setItem(DADAM_KEYS.USER_PROFILE, JSON.stringify(defaultProfile));
    return defaultProfile;
}

let currentUser = loadUserProfile();

/* 현재 유저 정보를 저장 + 헤더에 반영 */
function setCurrentUser(profile) {
    currentUser = {
        id: profile.id ?? currentUser.id ?? null,
        name: profile.name ?? currentUser.name ?? "우리 가족",
        avatarUrl:
            profile.avatarUrl ??
            profile.avatar ??
            profile.profileImageUrl ??
            currentUser.avatarUrl ??
            null,
        role: profile.role ?? profile.familyRole ?? currentUser.role ?? "child",
        familyRole: profile.familyRole ?? currentUser.familyRole ?? "child",
        familyCode: profile.familyCode ?? currentUser.familyCode ?? "",
        email: profile.email ?? currentUser.email ?? "",
    };

    localStorage.setItem(DADAM_KEYS.USER_PROFILE, JSON.stringify(currentUser));
    applyCurrentUserToHeader();
}

/* 전역 상태 currentUser 가 있다고 가정 */
function applyCurrentUserToHeader() {
    const nameEl = document.getElementById("current-username");
    const avatarWrapper = document.getElementById("current-avatar");

    if (!avatarWrapper) return;

    const name = currentUser?.name || "우리 가족";
    const avatarUrl =
        currentUser?.avatarUrl || currentUser?.profileImageUrl || null;

    if (nameEl) {
        nameEl.textContent = name;
    }

    // 공통 빌더 사용
    const html = buildAvatarHtml({
        userId: currentUser?.id ?? null,
        userName: name,
        avatarUrl,
        size: "sm",
        // 헤더는 기본 동그라미이므로 variant는 필요 시 "accent" 등으로
    });

    avatarWrapper.innerHTML = html;
}

/* -----------------------------------------------------
   💾 로컬스토리지 헬퍼
----------------------------------------------------- */

function save(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
}

function load(key, fallback = null) {
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
}

/* -----------------------------------------------------
   🔐 인증 토큰 헬퍼
----------------------------------------------------- */

function getAuthToken() {
    return localStorage.getItem(DADAM_KEYS.AUTH_TOKEN) || null;
}

function setAuthToken(token) {
    if (token) {
        localStorage.setItem(DADAM_KEYS.AUTH_TOKEN, token);
    } else {
        localStorage.removeItem(DADAM_KEYS.AUTH_TOKEN);
    }
}

function isLoggedIn() {
    return !!getAuthToken();
}

/* 화면 블러 + 로그인 강제 상태 전환 */
function setAuthUiState(loggedIn) {
    const appEl = document.querySelector(".app");
    if (!appEl) return;

    if (loggedIn) {
        appEl.classList.remove("is-blurred");
    } else {
        appEl.classList.add("is-blurred");
        openModal("modal-auth");
    }
}

/* -----------------------------------------------------
   🔔 알림(Notification) 시스템
----------------------------------------------------- */

function addNotification({ type = "info", message }) {
    const list = load(DADAM_KEYS.NOTIFICATIONS, []);

    const newItem = {
        id: Date.now(),
        type,
        message,
        time: new Date().toLocaleString(),
    };

    list.unshift(newItem);
    save(DADAM_KEYS.NOTIFICATIONS, list);

    showNotificationBadge(true);
}

function showNotificationBadge(active) {
    const badge = $("#notification-badge");
    if (!badge) return;
    if (active) badge.classList.add("is-active");
    else badge.classList.remove("is-active");
}

function renderNotifications() {
    const list = load(DADAM_KEYS.NOTIFICATIONS, []);
    const container = $("#notification-list");
    if (!container) return;

    if (list.length === 0) {
        container.innerHTML = `<li class="empty">아직 알림이 없어요</li>`;
        showNotificationBadge(false);
        return;
    }

    container.innerHTML = list
        .map(
            (n) => `
        <li class="notification-item">
          <div class="notification-text">
            <p class="notification-msg">${n.message}</p>
            <p class="notification-time">${n.time}</p>
          </div>
        </li>
      `
        )
        .join("");

    showNotificationBadge(false);
}

/* -----------------------------------------------------
   🪟 모달 시스템 (Common)
----------------------------------------------------- */

function openModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.add("is-active");
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.remove("is-active");
}

/* ESC로 닫기 – 로그인 강제 중엔 auth 모달은 닫히지 않음 */
document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
        document.querySelectorAll(".modal-backdrop.is-active").forEach((m) => {
            if (m.id === "modal-auth" && !isLoggedIn()) return;
            m.classList.remove("is-active");
        });
    }
});

/* 모달 닫기 버튼 */
document.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-close-modal]");
    if (!btn) return;
    const targetId = btn.dataset.closeModal;
    if (targetId === "modal-auth" && !isLoggedIn()) {
        // 로그인 전에는 로그인 모달을 강제로 유지
        return;
    }
    closeModal(targetId);
});

/* 모달 바깥(배경) 클릭 시 닫기 – auth는 로그인 전이면 유지 */
document.addEventListener("click", (e) => {
    if (!e.target.classList.contains("modal-backdrop")) return;
    if (e.target.id === "modal-auth" && !isLoggedIn()) return;
    e.target.classList.remove("is-active");
});

/* -----------------------------------------------------
   🔔 알림 모달 오픈 버튼들
----------------------------------------------------- */

$("#open-notifications")?.addEventListener("click", () => {
    renderNotifications();
    openModal("modal-notifications");
});

$("#open-notifications-from-card")?.addEventListener("click", () => {
    renderNotifications();
    openModal("modal-notifications");
});

/* -----------------------------------------------------
   👤 프로필 / 로그인 모달 오픈
----------------------------------------------------- */

$("#open-profile")?.addEventListener("click", () => {
    // 로그인 안 돼 있으면 프로필 대신 로그인 강제
    if (!isLoggedIn()) {
        setAuthUiState(false);
        return;
    }

    $("#profile-name-input").value = currentUser.name || "";
    $("#profile-role-input").value = currentUser.role || "child";

    const avatarPreview = $("#profile-avatar-preview");
    if (avatarPreview) {
        const label = getAvatarLabel(currentUser.name || "나");
        avatarPreview.innerHTML = `<span class="avatar-initial">${label}</span>`;
        if (currentUser.avatar) {
            avatarPreview.style.backgroundImage = `url(${currentUser.avatar})`;
            avatarPreview.style.backgroundSize = "cover";
            avatarPreview.style.backgroundPosition = "center";
        } else {
            avatarPreview.style.backgroundImage = "none";
        }
    }

    openModal("modal-profile");
});

$("#open-auth")?.addEventListener("click", () => {
    setAuthUiState(false);
});

/* -----------------------------------------------------
   🧪 알림 테스트 함수 (디버깅용)
----------------------------------------------------- */

window.dadamNotify = function (msg) {
    addNotification({ type: "info", message: msg });
    console.log("알림 추가:", msg);
};

/* -----------------------------------------------------
   👨‍👩‍👧‍👦 프로필 모달 내 가족 코드 / 로그아웃
----------------------------------------------------- */

document.addEventListener("DOMContentLoaded", () => {
    applyCurrentUserToHeader();

    // 초기 진입 시: 로그인 안 되어 있으면 화면 블러 + 로그인 모달
    setAuthUiState(isLoggedIn());

    const logoutBtn = document.getElementById("logout-btn");
    logoutBtn?.addEventListener("click", () => {
        setAuthToken(null);
        // 기본 프로필로 되돌림
        setCurrentUser(loadUserProfile());
        closeModal("modal-profile");
        setAuthUiState(false);
        addNotification({
            type: "info",
            message: "로그아웃되었어요.",
        });
    });

    const familyCheckBtn = document.getElementById("family-code-check-btn");
    familyCheckBtn?.addEventListener("click", () => {
        const input = document.getElementById("family-code-input");
        if (!input) return;
        const code = input.value.trim();
        if (!code) {
            alert("가족 코드를 입력해 주세요.");
            return;
        }
        // 실제 검증 API는 나중에 붙이면 됨
        addNotification({
            type: "info",
            message: `가족 코드 "${code}"를 확인했어요. (백엔드 연동 예정)`,
        });
    });
});
