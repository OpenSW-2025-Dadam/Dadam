/* =====================================================
   dadam.avatar.js
   - 아바타 공통 유틸
   - 이름 라벨 2글자 + 사진 배경 처리
===================================================== */

function getAvatarLabel(rawName) {
    if (!rawName) return "가족";

    const name = String(rawName).trim();
    if (name.length === 0) return "가족";

    const parts = name.split(/\s+/);
    const lastPart = parts[parts.length - 1];

    // 1) 마지막 파트에서 한글만 뽑기 (괄호, 이모지 등 제거)
    const hangulChars = lastPart.match(/[가-힣]/g);

    if (hangulChars && hangulChars.length > 0) {
        const pure = hangulChars.join(""); // "엄마(나)" -> "엄마"

        if (pure.length <= 2) {
            return pure;                 // 1~2글자는 그대로
        }
        return pure.slice(-2);          // 3글자 이상이면 뒤에서 2글자
    }

    // 2) 한글이 아닌 경우: 영문/숫자 이니셜 2글자
    const alnum = lastPart.replace(/[^A-Za-z0-9]/g, "");
    if (alnum.length === 0) {
        // 그래도 걸리는게 없으면 그냥 앞 2글자
        return lastPart.slice(0, 2);
    }
    return alnum.slice(0, 2).toUpperCase();
}


function safeEscape(text) {
    if (typeof escapeHtml === "function") {
        return escapeHtml(text);
    }
    return text;
}

/**
 * 공통 아바타 HTML 빌더
 *
 * @param {Object} options
 *   - userId: 사용자 ID (data-user-id 용, 없어도 됨)
 *   - userName: 이름 문자열
 *   - avatarUrl: 프로필 이미지 URL (null 이면 기본 이니셜)
 *   - size: "sm" | "md" | "lg"
 *   - variant: "", "soft", "accent" 등 클래스 추가
 */
function buildAvatarHtml(options = {}) {
    const {
        userId = null,
        userName = "가족",
        avatarUrl = null,
        size = "md",
        variant = "",
    } = options;

    const label = getAvatarLabel(userName);
    const safeLabel = safeEscape(label);

    const classes = ["avatar", `avatar-${size}`];
    if (variant) {
        classes.push(`avatar-${variant}`);
    }

    // ✅ 사진이 있을 때는 클래스 플래그만 달아줌
    if (avatarUrl) {
        classes.push("has-avatar-image");
    }

    const style = avatarUrl
        ? ` style="background-image:url('${avatarUrl}'); background-size:cover; background-position:center;"`
        : "";

    const dataUser = userId != null ? ` data-user-id="${userId}"` : "";

    // ✅ 항상 텍스트는 넣어 둔다 (이미지가 깨져도 글자는 보이게)
    return `
      <span class="${classes.join(" ")}"${style}${dataUser}>
        <span class="avatar-initial">${safeLabel}</span>
      </span>
    `;
}

/**
 * (선택) 가족 표시용 이름 변환
 */
function getDisplayNameForUser(userId, fallbackName) {
    if (typeof DADAM_FAMILY === "object" && DADAM_FAMILY && userId != null) {
        const member = DADAM_FAMILY[String(userId)];
        if (member && member.displayName) {
            return member.displayName;
        }
    }
    return fallbackName || "가족";
}
