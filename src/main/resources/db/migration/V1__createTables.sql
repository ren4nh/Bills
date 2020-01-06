CREATE TABLE public.bills (
    id bigint NOT NULL,
    correctedvalue numeric(19,2),
    duedate date,
    name character varying(255),
    daysoverdue bigint,
    paymentdate date,
    value numeric(19,2)
);

CREATE SEQUENCE public.seqbills
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;