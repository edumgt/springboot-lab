CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS t_text_content (
  id SERIAL PRIMARY KEY,
  content TEXT NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_subject (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  level INTEGER NOT NULL,
  level_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS t_user (
  id SERIAL PRIMARY KEY,
  user_uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
  user_name VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  real_name VARCHAR(100),
  age INTEGER,
  sex INTEGER,
  birth_day TIMESTAMP,
  phone VARCHAR(30),
  last_active_time TIMESTAMP,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modify_time TIMESTAMP,
  role INTEGER NOT NULL,
  image_path VARCHAR(255),
  status INTEGER NOT NULL,
  user_level INTEGER
);

CREATE TABLE IF NOT EXISTS t_question (
  id SERIAL PRIMARY KEY,
  question_type INTEGER NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subject_id INTEGER REFERENCES t_subject(id),
  create_user INTEGER REFERENCES t_user(id),
  score INTEGER NOT NULL,
  status INTEGER NOT NULL,
  correct VARCHAR(500),
  difficult INTEGER,
  info_text_content_id INTEGER REFERENCES t_text_content(id),
  grade_level INTEGER
);

CREATE TABLE IF NOT EXISTS t_exam_paper (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  question_count INTEGER NOT NULL,
  score INTEGER NOT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user INTEGER REFERENCES t_user(id),
  subject_id INTEGER REFERENCES t_subject(id),
  paper_type INTEGER NOT NULL,
  frame_text_content_id INTEGER REFERENCES t_text_content(id),
  suggest_time INTEGER,
  limit_start_time TIMESTAMP,
  limit_end_time TIMESTAMP,
  grade_level INTEGER
);

CREATE TABLE IF NOT EXISTS t_exam_paper_answer (
  id SERIAL PRIMARY KEY,
  exam_paper_id INTEGER REFERENCES t_exam_paper(id),
  create_user INTEGER REFERENCES t_user(id),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_score INTEGER,
  subject_id INTEGER REFERENCES t_subject(id),
  question_count INTEGER,
  question_correct INTEGER,
  paper_score INTEGER,
  do_time INTEGER,
  paper_type INTEGER,
  system_score INTEGER,
  status INTEGER,
  paper_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS t_exam_paper_question_customer_answer (
  id SERIAL PRIMARY KEY,
  question_id INTEGER REFERENCES t_question(id),
  question_score INTEGER,
  subject_id INTEGER REFERENCES t_subject(id),
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_user INTEGER REFERENCES t_user(id),
  text_content_id INTEGER REFERENCES t_text_content(id),
  exam_paper_id INTEGER REFERENCES t_exam_paper(id),
  question_type INTEGER,
  answer VARCHAR(500),
  customer_score INTEGER,
  exam_paper_answer_id INTEGER REFERENCES t_exam_paper_answer(id),
  do_right BOOLEAN,
  question_text_content_id INTEGER REFERENCES t_text_content(id)
);
