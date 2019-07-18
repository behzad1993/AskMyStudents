-- drop table if exists course_lessons;
-- drop table if exists lesson_quizzes;
-- drop table if exists quiz_questions;
-- drop table if exists question_answers;
-- drop table if exists lesson_feedbacks;
-- drop table if exists course;
-- drop table if exists lesson;
-- drop table if exists quiz;
-- drop table if exists question;
-- drop table if exists answer;
-- drop table if exists feedback;
insert into teacher (id, name, email, password, role, enabled, birthday, created, modified)
values ('1', 'Teacher', 'teacher@teacher.com', '$2a$10$RHpjPTT0sJwPJgrmG2WeNeJOyr94y9q39036x5qYKVIvQpPddVhaO', 'ADMIN',
        true,
        '1960-03-29', '2018-08-17 07:42:44.136',
        '2018-08-17 07:42:44.137');
insert into teacher (id, name, email, password, role, enabled, birthday, created, modified)
values ('2', 'User', 'user@user.com', '$2a$10$8mY4p43qAHw.hiQi/kWkN.pSuSD2PgqPvT6L7R0amwlOl6FUcpZ3m', 'USER', true,
        '1980-07-03', '2018-08-17 07:42:44.136',
        '2018-08-17 07:42:44.137');

insert into course (id, username, name, description)
values ('1', 'user@user.com', 'Mathe', 'Mathe fuer Idioten');
insert into course (id, username, name, description)
values ('2', 'teacher@teacher.com', 'Deutsch', 'Deutsch fuer Schlauberger');
insert into course (id, username, name, description)
values ('3', 'user@user.com', 'Biologie', 'Biologie fuer Erde');
insert into course (id, username, name, description)
values ('4', 'user@user.com', 'Chemie', 'Chemie fuer Chemiker');

insert into lesson (id, name, description, pin, is_active, course_id)
values (1, 'Einmaleins', 'Basics', 1578, false, 1);
insert into lesson (id, name, description, pin, is_active, course_id)
values (2, 'Addition', 'Basics', 6899, false, 1);
insert into lesson (id, name, description, pin, is_active, course_id)
values (3, 'Atome', 'Basics', 3212, false, 4);
insert into lesson (id, name, description, pin, is_active, course_id)
values (4, 'Grammatik', 'Basics', 4454, false, 2);

insert into quiz (id, name, description, lesson_id)
values (1, 'Big Quiz', 'Whole year quiz', 3);
insert into quiz (id, name, description, lesson_id)
values (2, 'Litte Quiz', 'Last month quiz', 2);
insert into quiz (id, name, description, lesson_id)
values (3, 'Basic Quiz', 'Basic stuff', 1);
insert into quiz (id, name, description, lesson_id)
values (4, 'Grammar Quiz', 'Grammar', 4);

insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (1, true, '1+1', 10, 1, 1, 1, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (2, true, '1*1', 10, 1, 1, 2, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (3, true, '1/1', 10, 1, 1, 3, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (4, true, '1-1', 10, 1, 1, 0, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (5, true, 'Was ist Pi?', 10, 1, 3, 1, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (6, false, 'Satz des Pytagoras beschreibt?', 10, 1, 3, 2, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (7, true, 'Der Unterschied eines Rechtecks und eines Quadrats ist? ', 10, 2, 3, 3, false);
insert into question (id, allowed, text, timer, question_type, quiz_id, order_index, random_order)
values (8, false, 'Punkt vor Strich', 10, 2, 3, 0, false);

insert into answer (id, answer_text, is_correct, question_id, order_index)
values (1, '0', false, 1, 0);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (2, '1', false, 1, 1);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (3, '2', true, 1, 2);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (4, '3', false, 1, 3);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (5, '3.1414', false, 5, 0);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (6, '3.1415', true, 5, 1);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (7, '3.0415', false, 5, 2);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (8, '3.0414', false, 5, 3);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (9,
        'Er besagt, dass bei einem rechtwinkligen Dreieck die Summe der Quadrate der beiden Katheten gleich dem Quadrat der Hypotenuse ist. ',
        true, 6, 0);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (10,
        'Er besagt, dass bei einem beliebigen Dreieck die Summe der Quadrate der beiden Katheten gleich dem Quadrat der Hypotenuse ist. ',
        false, 6, 1);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (11,
        'Er besagt, dass bei einem rechtwinkligen Dreieck das Produkt der beiden Katheten gleich dem Quadrat der Hypotenuse ist. ',
        false, 6, 2);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (12, 'Es gibt keinen.', false, 7, 0);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (13, 'Der Name.', false, 7, 1);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (14, 'Rechtecke haben nicht 4 gleichlange Kanten.', true, 7, 2);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (15, 'Quadrate haben 4 gleichlange Kanten.', true, 7, 3);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (16, 'Stimmt.', true, 8, 0);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (17, 'Stimmt nicht.', false, 8, 1);
insert into answer (id, answer_text, is_correct, question_id, order_index)
values (18, 'Ist egal.', false, 8, 2);



insert into course_lessons (course_id, lessons_id)
values (1, 1);
insert into course_lessons (course_id, lessons_id)
values (1, 2);
insert into course_lessons (course_id, lessons_id)
values (2, 4);
insert into course_lessons (course_id, lessons_id)
values (4, 3);

insert into lesson_quizzes (lesson_id, quizzes_id)
values (1, 3);
insert into lesson_quizzes (lesson_id, quizzes_id)
values (2, 2);
insert into lesson_quizzes (lesson_id, quizzes_id)
values (3, 1);
insert into lesson_quizzes (lesson_id, quizzes_id)
values (4, 4);

insert into quiz_questions (quiz_id, questions_id)
values (1, 1);
insert into quiz_questions (quiz_id, questions_id)
values (1, 2);
insert into quiz_questions (quiz_id, questions_id)
values (1, 3);
insert into quiz_questions (quiz_id, questions_id)
values (1, 4);
insert into quiz_questions (quiz_id, questions_id)
values (3, 5);
insert into quiz_questions (quiz_id, questions_id)
values (3, 6);
insert into quiz_questions (quiz_id, questions_id)
values (3, 7);
insert into quiz_questions (quiz_id, questions_id)
values (3, 8);


insert into question_answers (question_id, answers_id)
values (1, 1);
insert into question_answers (question_id, answers_id)
values (1, 2);
insert into question_answers (question_id, answers_id)
values (1, 3);
insert into question_answers (question_id, answers_id)
values (1, 4);
insert into question_answers (question_id, answers_id)
values (5, 5);
insert into question_answers (question_id, answers_id)
values (5, 6);
insert into question_answers (question_id, answers_id)
values (5, 7);
insert into question_answers (question_id, answers_id)
values (5, 8);
insert into question_answers (question_id, answers_id)
values (6, 9);
insert into question_answers (question_id, answers_id)
values (6, 10);
insert into question_answers (question_id, answers_id)
values (6, 11);
insert into question_answers (question_id, answers_id)
values (7, 12);
insert into question_answers (question_id, answers_id)
values (7, 13);
insert into question_answers (question_id, answers_id)
values (7, 14);
insert into question_answers (question_id, answers_id)
values (7, 15);
insert into question_answers (question_id, answers_id)
values (8, 16);
insert into question_answers (question_id, answers_id)
values (8, 17);
insert into question_answers (question_id, answers_id)
values (8, 18);