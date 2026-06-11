INSERT INTO t_text_content (id, content, create_time) VALUES
  (1, '{"title":"기본 수학 문제","question":"1 + 1 = ?"}', NOW()),
  (2, '{"title":"모의고사 1회","description":"샘플 시험지"}', NOW()),
  (3, '{"questionId":1,"answer":"2"}', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_subject (id, name, level, level_name) VALUES
  (1, '수학', 1, '중1')
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_user (id, user_uuid, user_name, password, real_name, age, sex, birth_day, phone,
                    last_active_time, create_time, modify_time, role, image_path, status, user_level)
VALUES
  (1, uuid_generate_v4(), 'admin', '21232f297a57a5a743894a0e4a801fc3', '관리자', 30, 1,
   NOW() - INTERVAL '30 years', '010-1111-2222', NOW(), NOW(), NOW(), 1, NULL, 1, 1),
  (2, uuid_generate_v4(), 'student1', '81dc9bdb52d04dc20036dbd8313ed055', '홍길동', 18, 1,
   NOW() - INTERVAL '18 years', '010-9999-0000', NOW(), NOW(), NOW(), 2, NULL, 1, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_question (id, question_type, create_time, subject_id, create_user, score, status, correct, difficult, info_text_content_id, grade_level)
VALUES (1, 1, NOW(), 1, 1, 10, 1, '2', 1, 1, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_exam_paper (id, name, question_count, score, create_time, create_user, subject_id, paper_type, frame_text_content_id, suggest_time, limit_start_time, limit_end_time, grade_level)
VALUES (1, '수학 샘플 시험지', 1, 10, NOW(), 1, 1, 1, 2, 10, NOW(), NOW() + INTERVAL '7 days', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_exam_paper_answer (id, exam_paper_id, create_user, create_time, user_score, subject_id, question_count, question_correct, paper_score, do_time, paper_type, system_score, status, paper_name)
VALUES (1, 1, 2, NOW(), 10, 1, 1, 1, 10, 5, 1, 10, 2, '수학 샘플 시험지')
ON CONFLICT (id) DO NOTHING;

INSERT INTO t_exam_paper_question_customer_answer (id, question_id, question_score, subject_id, create_time, create_user, text_content_id, exam_paper_id, question_type, answer, customer_score, exam_paper_answer_id, do_right, question_text_content_id)
VALUES (1, 1, 10, 1, NOW(), 2, 3, 1, 1, '2', 10, 1, TRUE, 1)
ON CONFLICT (id) DO NOTHING;

SELECT setval('t_text_content_id_seq', COALESCE((SELECT MAX(id) FROM t_text_content), 1), true);
SELECT setval('t_subject_id_seq', COALESCE((SELECT MAX(id) FROM t_subject), 1), true);
SELECT setval('t_user_id_seq', COALESCE((SELECT MAX(id) FROM t_user), 1), true);
SELECT setval('t_question_id_seq', COALESCE((SELECT MAX(id) FROM t_question), 1), true);
SELECT setval('t_exam_paper_id_seq', COALESCE((SELECT MAX(id) FROM t_exam_paper), 1), true);
SELECT setval('t_exam_paper_answer_id_seq', COALESCE((SELECT MAX(id) FROM t_exam_paper_answer), 1), true);
SELECT setval('t_exam_paper_question_customer_answer_id_seq', COALESCE((SELECT MAX(id) FROM t_exam_paper_question_customer_answer), 1), true);
