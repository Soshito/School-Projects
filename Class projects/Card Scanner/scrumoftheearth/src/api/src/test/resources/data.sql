------------------------------------------------------------------------------------------------
-- Name: Gavin Papenthien
-- Project: Attendance App - This is a full stack attendance tracking and management software.
-- Purpose: Create mock data to test the API with.
------------------------------------------------------------------------------------------------
INSERT INTO roles (name) VALUES
('Administrator'),
('Professor'),
('Student');

INSERT INTO users (first_name, last_name, student_card_id, username, password, role_id) VALUES
('Gavin', 'Papenthien', 'ABC123', 'papengav', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 3),
('Sam', 'Miller', 'DEF456', 'millerSam', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 3),
('admin', 'admin', '', 'admin', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 1),
('professor', 'professor', '', 'professor', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 2),
('I', 'will', 'be', 'deleted', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 3),
('I', 'have', 'no', 'enrollments', '$2a$12$JeEd1mOtw8w7SRuApO2KNe958L3iqiTFJYW2IxL3YXyfacoare4Cy', 3);

INSERT INTO courses (name, section_count) VALUES
('SE3330', 2),
('SE3020', 10);

INSERT INTO sections (room_num, num_students, course_id, professor_id) VALUES
(1, 1, 1, 4),
(10, 10, 2, 4);

INSERT INTO enrollments (section_id, student_id) VALUES
(1, 1),
(2, 2);

INSERT INTO meeting_times (section_id, day_of_week, start_time, end_time) VALUES
(1, DAY_OF_WEEK(CURRENT_DATE()), CURRENT_TIME() + 10 MINUTE, CURRENT_TIME() + 1 MINUTE),
(2, DAY_OF_WEEK(CURRENT_DATE() + 1 DAY), CURRENT_TIME() + 10 MINUTE, CURRENT_TIME() + 1 MINUTE);