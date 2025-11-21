SET FOREIGN_KEY_CHECKS = 0;
-- 1. 기존 데이터 초기화 (테스트 반복을 위해)
DELETE FROM answer;
DELETE FROM question;
DELETE FROM app_user;

-- Auto Increment 카운터 재설정 (ID 충돌 방지)
ALTER TABLE app_user AUTO_INCREMENT = 1;
ALTER TABLE question AUTO_INCREMENT = 1;
ALTER TABLE answer AUTO_INCREMENT = 1;

-- 2. 테스트 사용자 삽입
INSERT INTO app_user (email, name, password) VALUES
('parent_user_1@dadam.com', '부모님', 'test_password'),
('child_user_2@dadam.com', '자녀1', 'test_password'),
('child_user_3@dadam.com', '자녀2', 'test_password');

-- 3. 테스트 질문 삽입
INSERT INTO question (content, category, created_at) VALUES
('가족과 함께한 가장 즐거웠던 여행은 무엇인가요?', 'TRAVEL', NOW()),
('요즘 가족 구성원 각자가 빠져 있는 취미는?', 'HOBBY', NOW()),
('서로에게 가장 고마웠던 순간 하나씩 이야기해 볼까요?', 'MEMORY', NOW());

-- 4. 테스트 답변 삽입 (User ID 1, 2, 3은 위 INSERT로 자동 할당됨)
-- Question ID 1에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(1, 2, '작년에 갔던 해외여행! 공항에서 길 잃어버릴 뻔한 게 짜릿했어요.', NOW()),
(1, 3, '그냥 집 근처 공원에서 텐트 치고 놀았던 주말이 제일 편하고 즐거웠어요.', NOW() + INTERVAL 1 MINUTE);

-- Question ID 2에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(2, 1, '요즘 저는 주말에 새로운 레시피로 요리하는 것에 푹 빠져 있어요.', NOW()),
(2, 2, '저는 게임이요! 특히 다같이 할 수 있는 보드 게임을 다시 모으고 있어요!!', NOW() + INTERVAL 1 MINUTE),
(2, 3, '저는 그림 그리기요. 가족들 몰래 방에서 열심히 그리고 있어요.', NOW() + INTERVAL 2 MINUTE);

-- Question ID 3에 대한 답변
INSERT INTO answer (question_id, user_id, content, created_at) VALUES
(3, 1, '힘들 때 말없이 어깨를 토닥여준 순간이 가장 고마웠어요.', NOW()),
(3, 2, '생일날 깜짝 이벤트 해줬을 때요! 평생 잊지 못할 거예요.', NOW() + INTERVAL 1 MINUTE),
(3, 3, '제가 잘못했을 때 혼내지 않고 차분히 이야기해 줬던 그날이 기억에 남아요.', NOW() + INTERVAL 2 MINUTE);

SET FOREIGN_KEY_CHECKS = 1;