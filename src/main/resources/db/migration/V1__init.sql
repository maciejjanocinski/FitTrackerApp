CREATE TABLE diary
(
    diary_id           INT AUTO_INCREMENT PRIMARY KEY,
    sum_kcal           DECIMAL(10, 2),
    sum_protein        DECIMAL(10, 2),
    sum_carbohydrates  DECIMAL(10, 2),
    sum_fat            DECIMAL(10, 2),
    sum_fiber          DECIMAL(10, 2),
    goal_kcal          DECIMAL(10, 2),
    goal_protein       DECIMAL(10, 2),
    goal_fat           DECIMAL(10, 2),
    goal_carbohydrates DECIMAL(10, 2),
    goal_fiber         DECIMAL(10, 2),
    left_kcal          DECIMAL(10, 2),
    left_protein       DECIMAL(10, 2),
    left_fat           DECIMAL(10, 2),
    left_carbohydrates DECIMAL(10, 2),
    left_fiber         DECIMAL(10, 2)
);

CREATE TABLE product_in_diary
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    product_id     VARCHAR(255),
    product_name   VARCHAR(255),
    kcal          DECIMAL(10, 2),
    protein       DECIMAL(10, 2),
    carbohydrates DECIMAL(10, 2),
    fat           DECIMAL(10, 2),
    fiber         DECIMAL(10, 2),
    image         VARCHAR(255),
    measure_label  VARCHAR(255),
    quantity      DECIMAL(10, 2),
    diary_id       INT,
    FOREIGN KEY (diary_id) REFERENCES diary (diary_id)
);

CREATE TABLE user
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    username         VARCHAR(255) UNIQUE,
    name             VARCHAR(255) NOT NULL,
    surname          VARCHAR(255) NOT NULL,
    gender           VARCHAR(255),
    email            VARCHAR(255) UNIQUE NOT NULL,
    phone            VARCHAR(9) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    diary_id         INT,
    last_product_query VARCHAR(255),
    last_recipe_query  VARCHAR(255),
    FOREIGN KEY (diary_id) REFERENCES diary (diary_id)
);

CREATE TABLE role (
                      id   INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) UNIQUE
);

CREATE TABLE users_roles
(
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE recipe
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    label              VARCHAR(255),
    image              TEXT,
    source             VARCHAR(255),
    url                VARCHAR(255),
    yield              INT,
    calories_per_serving DOUBLE,
    protein_per_serving  DOUBLE,
    carbs_per_serving    DOUBLE,
    fat_per_serving      DOUBLE,
    fiber_per_serving    DOUBLE,
    is_used             BOOLEAN,
    query              VARCHAR(255),
    user_id             INT,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE ingredient_line
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    text      VARCHAR(255),
    recipe_id INT,
    FOREIGN KEY (recipe_id) REFERENCES recipe (id)
);

CREATE TABLE product
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    product_id     VARCHAR(255),
    name          VARCHAR(255),
    kcal          DECIMAL(10, 2),
    protein       DECIMAL(10, 2),
    fat           DECIMAL(10, 2),
    carbohydrates DECIMAL(10, 2),
    fiber         DECIMAL(10, 2),
    image         VARCHAR(255),
    is_used        BOOLEAN,
    query         VARCHAR(255),
    user_id       INT,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE measure
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255),
    weight   DECIMAL(10, 2),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES product (id)
);
