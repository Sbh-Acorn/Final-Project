-- 유저 테이블
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,  -- 아이디 (PK)
    first_name VARCHAR(255),           -- 이름
    last_name VARCHAR(255),            -- 성
    pccc VARCHAR(255),                -- 통관번호
    email VARCHAR(255),                -- 이메일
    password VARCHAR(255),             -- 비밀번호
    phone_number VARCHAR(20),          -- 전화번호
    birth_date DATE,                   -- 생일
    join_date DATETIME,                -- 가입일자
    is_deleted BOOLEAN DEFAULT FALSE   -- 삭제여부
);

-- 브랜드 테이블
CREATE TABLE Brand (
    brand_id INT AUTO_INCREMENT PRIMARY KEY,  -- 브랜드ID (PK)
    name VARCHAR(255),                        -- 이름
    logo_url VARCHAR(255),                    -- 로고 URL
    description TEXT,                         -- 설명
    is_deleted BOOLEAN DEFAULT FALSE          -- 삭제 여부 (기본값: FALSE)
);

-- 카테고리 테이블 (대분류, 소분류, 세분류)
CREATE TABLE Category1 (
    category1_id INT AUTO_INCREMENT PRIMARY KEY,  -- 카테고리1 ID (PK, AUTO_INCREMENT)
    name VARCHAR(255)                             -- 카테고리 이름
);

CREATE TABLE Category2 (
    category2_id INT AUTO_INCREMENT PRIMARY KEY,  -- 카테고리2 ID (PK, AUTO_INCREMENT)
    name VARCHAR(255),                            -- 카테고리2 이름
    category1_id INT,                             -- 카테고리1 ID (FK)
    FOREIGN KEY (category1_id) REFERENCES Category1(category1_id)
);

CREATE TABLE Category3 (
    category3_id INT AUTO_INCREMENT PRIMARY KEY,  -- 카테고리3 ID (PK, AUTO_INCREMENT)
    name VARCHAR(255),                            -- 카테고리3 이름
    category2_id INT,                             -- 카테고리2 ID (FK)
    FOREIGN KEY (category2_id) REFERENCES Category2(category2_id)
);

-- 주소 테이블
CREATE TABLE Address (
    address_id INT AUTO_INCREMENT PRIMARY KEY,  -- 주소ID(PK)
    user_id INT,              -- 유저아이디 (FK)
    address VARCHAR(255),                 -- 주소
    detail VARCHAR(255),       -- 상세주소
    zip_code VARCHAR(20),           -- 우편번호
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);


-- 제품 테이블
CREATE TABLE Product (
    product_id VARCHAR(255) PRIMARY KEY,         -- 제품ID (PK)
    brand_id INT,                                -- 브랜드ID (FK)
    image_url VARCHAR(255),                      -- 이미지 URL
    name VARCHAR(255),                           -- 제품 이름
    original_price DECIMAL(10, 2),               -- 정가
    current_price DECIMAL(10, 2),                -- 현재판매가
    description TEXT,                            -- 설명
    category1_id INT,                            -- 카테고리1 ID (FK)
    category2_id INT,                            -- 카테고리2 ID (FK)
    category3_id INT,                            -- 카테고리3 ID (FK)
    is_fta BOOLEAN DEFAULT FALSE,                -- FTA 여부
    is_deleted BOOLEAN DEFAULT FALSE,            -- 삭제 여부
    is_excludedVoucher BOOLEAN DEFAULT FALSE,    -- 쿠폰 적용 제외 여부
    FOREIGN KEY (brand_id) REFERENCES Brand(brand_id),
    FOREIGN KEY (category1_id) REFERENCES Category1(category1_id),
    FOREIGN KEY (category2_id) REFERENCES Category2(category2_id),
    FOREIGN KEY (category3_id) REFERENCES Category3(category3_id)
);

-- 장바구니 테이블
CREATE TABLE Cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    product_id VARCHAR(255),
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);


CREATE TABLE Post (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255),
    title VARCHAR(255),
    content TEXT,
    user_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    views INT DEFAULT 0,
    likes INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE PostImage (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT,
    image_url VARCHAR(255),
    FOREIGN KEY (post_id) REFERENCES Post(post_id)
);

CREATE TABLE Comment (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    post_id INT,
    user_id INT,
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE PostLike (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT,
    user_id INT,
    FOREIGN KEY (post_id) REFERENCES Post(post_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE PriceDrop (
    drop_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    previous_price INT NOT NULL,
    current_price INT NOT NULL,
    created_at DATETIME NOT NULL,
    notification_sent BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

-- 나머지 테이블 추가 (순서 유지)
CREATE TABLE Wishlist (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    product_id VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);

CREATE TABLE Notification (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);
