# Quiz Management: Spray - Akka - Slick

Build a REST api with Spray and Akka and Slick

## DataBase
```
CREATE TABLE public.accounts
(
    id uuid NOT NULL,
    firt_name character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    email character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    phone character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    date_of_birth date NOT NULL,
    sex character varying(256) COLLATE pg_catalog."default",
    nick_name character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    user_name character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    created bigint NOT NULL,
    last_updated bigint NOT NULL,
    CONSTRAINT accounts_pkey PRIMARY KEY (id),
    CONSTRAINT accounts_email_key UNIQUE (email),
    CONSTRAINT accounts_phone_key UNIQUE (phone),
    CONSTRAINT accounts_user_name_key UNIQUE (user_name)
);

CREATE TABLE public.account_friends
(
    account_id uuid NOT NULL,
    friend_id uuid NOT NULL,
    status smallint NOT NULL,
    created bigint NOT NULL,
    last_updated bigint NOT NULL,
    CONSTRAINT account_friends_pkey PRIMARY KEY (account_id, friend_id),
    CONSTRAINT account_friends_account_id_fkey FOREIGN KEY (account_id)
        REFERENCES public.accounts (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT account_friends_friend_id_fkey FOREIGN KEY (friend_id)
        REFERENCES public.accounts (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
```

## Run the service:
```
> sbt run
```

The service runs on port 5000 by default.

## Usage

### Create a Account
...
