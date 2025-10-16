-- Crear ENUMS
CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE auth_provider_enum AS ENUM ('LOCAL', 'GOOGLE', 'AUTHZERO', 'X');
CREATE TYPE user_role_enum AS ENUM ('CREATOR', 'BUSINESS', 'ADMIN', 'STAFF'); -- Ajusta a tus valores reales de UserRole

-- Crear tabla "user"

CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    email_verify BOOLEAN DEFAULT FALSE,
    username VARCHAR(100),
    avatar_url VARCHAR(500),
    user_role user_role_enum NOT NULL,
    currency VARCHAR(10),
    country VARCHAR(100),
    city VARCHAR(100),
    auth_provider auth_provider_enum NOT NULL,
    external_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status status_enum DEFAULT 'ACTIVE'
);

-- Crear tabla "wallet"

CREATE TABLE wallet (
    id BIGSERIAL PRIMARY KEY,
    usd NUMERIC(15,2) DEFAULT 0 NOT NULL,
    pen NUMERIC(15,2) DEFAULT 0 NOT NULL,
    user_id BIGINT UNIQUE NOT NULL,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

-- Crear ENUM para plataformas de redes sociales
CREATE TYPE platform_enum AS ENUM ('TIKTOK', 'INSTAGRAM', 'YOUTUBE','KICK', 'TWITCH', 'FACEBOOK', 'TWITTER');

-- Crear tabla "social_media"

CREATE TABLE social_media (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    platform platform_enum NOT NULL,
    link TEXT,
    connected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(255),
    avatar TEXT,
    nick_name VARCHAR(255),
    CONSTRAINT fk_socialmedia_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);


-- Enums
CREATE TYPE business_type_enum AS ENUM ('STREAMER', 'BRAND');
CREATE TYPE creator_type_enum AS ENUM ('CLIPPER', 'UGC');

-- Business Table
CREATE TABLE business (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    id_user BIGINT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    business_type business_type_enum NOT NULL,
    sector VARCHAR(255) NOT NULL,
    CONSTRAINT fk_business_user FOREIGN KEY (id_user) REFERENCES "user"(id)
);

-- Creator Table
CREATE TABLE creator (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    id_user BIGINT NOT NULL UNIQUE,
    birthday DATE NOT NULL,
    creator_type creator_type_enum NOT NULL,
    CONSTRAINT fk_creator_user FOREIGN KEY (id_user) REFERENCES "user"(id)
);


-- Enums for Campaign
CREATE TYPE currency_enum AS ENUM (
    'USD',
    'EUR',
    'GBP',
    'JPY',
    'PEN'
);

-- Payment Status
CREATE TYPE payment_status_enum AS ENUM (
    'PENDING',
    'APPROVED',
    'DONE',
    'REJECTED'
);

-- Campaign Status
CREATE TYPE campaign_status_enum AS ENUM (
    'PENDING',
    'IN_COMING',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED',
    'REJECTED',
    'DELETED',
    'BANNED'
);

-- Campaign table
CREATE TABLE campaign (
    id BIGSERIAL PRIMARY KEY,
    id_business BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type creator_type_enum NOT NULL,
    logo VARCHAR(255),
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    text_influencer TEXT,
    stablishment_place VARCHAR(255),
    currency currency_enum NOT NULL,
    cpm NUMERIC(19,2) NOT NULL,
    total_budget NUMERIC(19,2) NOT NULL,
    consumed_budget NUMERIC(19,2) NOT NULL DEFAULT 0,
    maximun_payment NUMERIC(19,2) NOT NULL,
    minimum_payment NUMERIC(19,2) NOT NULL,
    campaign_status campaign_status_enum NOT NULL DEFAULT 'PENDING',
    payment_status payment_status_enum NOT NULL DEFAULT 'PENDING',
    has_tiktok BOOLEAN DEFAULT FALSE,
    has_instagram BOOLEAN DEFAULT FALSE,
    has_facebook BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_campaign_business FOREIGN KEY (id_business) REFERENCES business (id)
);


CREATE TYPE payment_type_enum AS ENUM ('PAGO_EFECTIVO','VISA','MASTERCARD','BANK_TRANSFER'); -- Ajusta a tus valores reales

-- Tabla CampaignPayment
CREATE TABLE campaign_payment (
    id BIGSERIAL PRIMARY KEY,
    id_campaign BIGINT NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    payment_status payment_status_enum NOT NULL DEFAULT 'PENDING',
    currency currency_enum NOT NULL,
    payment_type payment_type_enum NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ruc VARCHAR(50),
    business_name VARCHAR(255) NOT NULL,

    CONSTRAINT fk_campaign_payment_campaign
        FOREIGN KEY (id_campaign) REFERENCES campaign (id) ON DELETE CASCADE
);


CREATE TYPE submission_status_enum AS ENUM ('PENDING', 'APPROVED', 'REJECTED'); -- Ajusta a tus valores reales

CREATE TABLE submission (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL, 
    creator_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    video_url VARCHAR(500) NOT NULL,
    platform platform_enum,
    saved_video_url VARCHAR(500) NOT NULL UNIQUE,
    description TEXT,
    percentage INT,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    submission_status submission_status_enum NOT NULL,
    name VARCHAR(255),
    avatar VARCHAR(500),
    nickname VARCHAR(255),
    profile_url VARCHAR(500),
    CONSTRAINT fk_submission_creator FOREIGN KEY (creator_id) REFERENCES creator (id),
    CONSTRAINT fk_submission_campaign FOREIGN KEY (campaign_id) REFERENCES campaign (id)
);

CREATE TABLE submission_payments (
    id BIGSERIAL PRIMARY KEY,
    submission_id BIGINT NOT NULL UNIQUE,
    payment NUMERIC(18,4) NOT NULL,
    payment_received NUMERIC(18,4) NOT NULL,
    engagement NUMERIC(5,2),
    views INT NOT NULL,
    likes INT NOT NULL,
    comments INT NOT NULL,
    share_count INT,
    "timestamp" VARCHAR(255),
    caption TEXT,
    display_url TEXT,
    payment_status payment_status_enum NOT NULL,

    CONSTRAINT fk_submission_payment_submission 
        FOREIGN KEY (submission_id) REFERENCES submission (id) ON DELETE CASCADE
);
