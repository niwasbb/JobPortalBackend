CREATE TABLE  users (   user_id BINARY(16) PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        email_id VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        CONSTRAINT chk_user_role CHECK (role IN ('JOB_SEEKER', 'RECRUITER', 'ADMIN'))
);

CREATE TABLE recruiter (   user_id BINARY(16) NOT NULL,
                           email_id VARCHAR(255),
                           first_name VARCHAR(255),
                           last_name VARCHAR(255),
                           company_name VARCHAR(255),
                           industry_type VARCHAR(255),

                           PRIMARY KEY (user_id),
                           CONSTRAINT fk_recruiter_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(user_id)
                                   ON DELETE CASCADE
);

CREATE TABLE job_seeker (   user_id BINARY(16) NOT NULL,
                            first_name VARCHAR(255),
                            last_name VARCHAR(255),
                            email_id VARCHAR(255),
                            phone_number VARCHAR(20),
                            location VARCHAR(255),
                            resume VARCHAR(255),

                            PRIMARY KEY (user_id),
                            CONSTRAINT fk_job_seeker_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(user_id)
                                    ON DELETE CASCADE
);

CREATE TABLE job_seeker_skills (job_seeker_user_id BINARY(16) NOT NULL,
                                   skills VARCHAR(255),

                                   CONSTRAINT fk_skills_job_seeker
                                       FOREIGN KEY (job_seeker_user_id)
                                           REFERENCES job_seeker(user_id)
                                           ON DELETE CASCADE
);

CREATE TABLE job_seeker_education (job_seeker_user_id BINARY(16) NOT NULL,
                                      education VARCHAR(255),

                                      CONSTRAINT fk_education_job_seeker
                                          FOREIGN KEY (job_seeker_user_id)
                                              REFERENCES job_seeker(user_id)
                                              ON DELETE CASCADE
);

CREATE TABLE job_seeker_experience (job_seeker_user_id BINARY(16) NOT NULL,
                                       experience VARCHAR(255),

                                       CONSTRAINT fk_experience_job_seeker
                                           FOREIGN KEY (job_seeker_user_id)
                                               REFERENCES job_seeker(user_id)
                                               ON DELETE CASCADE
);

CREATE TABLE job_post (job_id BINARY(16) PRIMARY KEY,
                          posted_date TIMESTAMP,
                          title VARCHAR(255) NOT NULL,
                          job_description TEXT,
                          company_name VARCHAR(255) NOT NULL,
                          location VARCHAR(255) NOT NULL,
                          required_education VARCHAR(255) NOT NULL,
                          required_experience VARCHAR(255) NOT NULL,
                          no_of_vacancy INT DEFAULT 1,
                          salary_range VARCHAR(255),
                          recruiter_id BINARY(16) NOT NULL,

                          CONSTRAINT fk_jobpost_recruiter
                              FOREIGN KEY (recruiter_id)
                                  REFERENCES recruiter(user_id)
                                  ON DELETE CASCADE
);

CREATE TABLE job_post_required_skills (job_post_job_id BINARY(16) NOT NULL,
                                          required_skills VARCHAR(255),

                                          CONSTRAINT fk_jobpost_skills
                                              FOREIGN KEY (job_post_job_id)
                                                  REFERENCES job_post(job_id)
                                                  ON DELETE CASCADE
);


CREATE TABLE job_application (application_id BINARY(16) PRIMARY KEY,
                                 job_post_id BINARY(16) NOT NULL,
                                 applicant_id BINARY(16) NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 resume VARCHAR(255),

                                 CONSTRAINT chk_status CHECK (status IN('APPLIED', 'SHORTLISTED', 'REJECTED', 'CANCELED')),

                                 CONSTRAINT fk_job_application_job_post
                                     FOREIGN KEY (job_post_id)
                                         REFERENCES job_post(job_id),

                                 CONSTRAINT fk_job_application_job_seeker
                                     FOREIGN KEY (applicant_id)
                                         REFERENCES job_seeker(user_id)

);