--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

-- Started on 2021-12-02 11:42:39

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3388 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 209 (class 1259 OID 25323)
-- Name: assets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.assets (
    assetcode character varying(255) NOT NULL,
    assetname character varying(255),
    installdate timestamp without time zone NOT NULL,
    isdeleted boolean NOT NULL,
    specification character varying(255),
    state smallint NOT NULL,
    categorycode character varying(255) NOT NULL,
    locationid bigint NOT NULL
);


ALTER TABLE public.assets OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 25330)
-- Name: assignments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.assignments (
    assignmentid bigint NOT NULL,
    assigneddate timestamp without time zone,
    isdeleted boolean,
    note character varying(255),
    state smallint,
    assetcode character varying(255) NOT NULL,
    assignedby character varying(255) NOT NULL,
    assignedto character varying(255) NOT NULL
);


ALTER TABLE public.assignments OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 25337)
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    categorycode character varying(255) NOT NULL,
    categoryname character varying(255)
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 25390)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 25391)
-- Name: location_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.location_sequence
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.location_sequence OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 25344)
-- Name: locations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.locations (
    locationid bigint NOT NULL,
    address character varying(255)
);


ALTER TABLE public.locations OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 25349)
-- Name: requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.requests (
    requestid bigint NOT NULL,
    isdeleted boolean,
    returneddate timestamp without time zone,
    state smallint,
    acceptedby character varying(255),
    assignmentid bigint NOT NULL,
    requestedby character varying(255) NOT NULL
);


ALTER TABLE public.requests OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 25356)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    roleid bigint NOT NULL,
    rolename character varying(255) NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 25361)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    staffcode character varying(255) NOT NULL,
    dateofbirth timestamp without time zone,
    firstlogin boolean,
    firstname character varying(255),
    gender character varying(255),
    isdeleted boolean,
    joineddate timestamp without time zone,
    lastname character varying(255),
    password character varying(255),
    username character varying(255),
    locationid bigint,
    roleid bigint NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 3374 (class 0 OID 25323)
-- Dependencies: 209
-- Data for Name: assets; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3375 (class 0 OID 25330)
-- Dependencies: 210
-- Data for Name: assignments; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3376 (class 0 OID 25337)
-- Dependencies: 211
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3377 (class 0 OID 25344)
-- Dependencies: 212
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.locations VALUES (101, 'HCM');
INSERT INTO public.locations VALUES (102, 'HN');


--
-- TOC entry 3378 (class 0 OID 25349)
-- Dependencies: 213
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 3379 (class 0 OID 25356)
-- Dependencies: 214
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.roles VALUES (1001, 'ROLE_USER');
INSERT INTO public.roles VALUES (1002, 'ROLE_ADMIN');
INSERT INTO public.roles VALUES (1003, 'ROLE_USER_LOCKED');
INSERT INTO public.roles VALUES (1004, 'ROLE_ADMIN_LOCKED');


--
-- TOC entry 3380 (class 0 OID 25361)
-- Dependencies: 215
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.users VALUES ('SD0002', '2000-06-01 05:00:00', false, 'Nhi', 'Female', false, '2021-08-06 12:30:00', 'Mai Hoang', '$2a$10$OLjR1nFlun7W1qhg98K4k.YVtGva/cqv2F.kN8RH8TRDJ8kaOjxCy', 'nhimh1', 101, 1002);
INSERT INTO public.users VALUES ('SD0005', '2000-06-01 05:00:00', false, 'Nhi', 'Female', false, '2021-08-06 12:30:00', 'Mai Hoang', '$2a$10$OLjR1nFlun7W1qhg98K4k.YVtGva/cqv2F.kN8RH8TRDJ8kaOjxCy', 'nhimh4', 101, 1001);
INSERT INTO public.users VALUES ('SD0006', '2000-06-01 05:00:00', false, 'Nhi', 'Female', false, '2021-08-06 12:30:00', 'Mai Hoang', '$2a$10$G9L9OcCBIGTgaAIw19s9yO28qrG51R/wUSj7Ain0wNhu7t24O2VxC', 'nhimh5', 102, 1001);
INSERT INTO public.users VALUES ('SD0001', '2000-06-01 00:00:00', true, 'Nhi', 'Female', false, '2021-11-25 16:04:13.288292', 'Mai Hoang', '$2a$10$UHkYUDhlhu8zGJQcP/Ywp.Xqf042NDh4.p7YVAtAl.aFuImosre5m', 'nhimh', 101, 1001);
INSERT INTO public.users VALUES ('SD0003', '2000-06-01 05:00:00', false, 'Nhi', 'Female', false, '2021-08-06 12:30:00', 'Mai Hoang', '$2a$10$I76cGpKfHyKEVxHBakL6eeyptWa/K9rtAN3tyjDDKOPLf.83.rqmO', 'nhimh2', 102, 1002);
INSERT INTO public.users VALUES ('SD0004', '2000-06-01 05:00:00', false, 'Nhi', 'Female', false, '2021-08-06 12:30:00', 'Mai Hoang', '$2a$10$lBt5n/7ejwjZwO4dtArHhOoGhS/yrcJhiK7NeaAoo3hmbYvqQ3/WG', 'nhimh3', 101, 1001);


--
-- TOC entry 3389 (class 0 OID 0)
-- Dependencies: 216
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);


--
-- TOC entry 3390 (class 0 OID 0)
-- Dependencies: 217
-- Name: location_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.location_sequence', 100, false);


--
-- TOC entry 3193 (class 2606 OID 25329)
-- Name: assets assets_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT assets_pkey PRIMARY KEY (assetcode);


--
-- TOC entry 3199 (class 2606 OID 25336)
-- Name: assignments assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (assignmentid);


--
-- TOC entry 3201 (class 2606 OID 25343)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (categorycode);


--
-- TOC entry 3206 (class 2606 OID 25348)
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (locationid);


--
-- TOC entry 3214 (class 2606 OID 25355)
-- Name: requests requests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pkey PRIMARY KEY (requestid);


--
-- TOC entry 3217 (class 2606 OID 25360)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (roleid);


--
-- TOC entry 3208 (class 2606 OID 25379)
-- Name: locations uk6ythtk02shyto7rh04jhxi4h9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT uk6ythtk02shyto7rh04jhxi4h9 UNIQUE (address);


--
-- TOC entry 3204 (class 2606 OID 25377)
-- Name: categories ukpr2ms98ayaf1r17k7yyr4l3o9; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT ukpr2ms98ayaf1r17k7yyr4l3o9 UNIQUE (categoryname);


--
-- TOC entry 3219 (class 2606 OID 25389)
-- Name: users ukr43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- TOC entry 3224 (class 2606 OID 25367)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (staffcode);


--
-- TOC entry 3189 (class 1259 OID 25369)
-- Name: asset_category_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX asset_category_idx ON public.assets USING btree (categorycode);


--
-- TOC entry 3190 (class 1259 OID 25368)
-- Name: asset_name_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX asset_name_idx ON public.assets USING btree (assetname);


--
-- TOC entry 3191 (class 1259 OID 25370)
-- Name: asset_state_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX asset_state_idx ON public.assets USING btree (state);


--
-- TOC entry 3194 (class 1259 OID 25372)
-- Name: assignment_assetcode_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX assignment_assetcode_idx ON public.assignments USING btree (assetcode);


--
-- TOC entry 3195 (class 1259 OID 25373)
-- Name: assignment_assigneddate_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX assignment_assigneddate_idx ON public.assignments USING btree (assigneddate);


--
-- TOC entry 3196 (class 1259 OID 25371)
-- Name: assignment_assignedto_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX assignment_assignedto_idx ON public.assignments USING btree (assignedto);


--
-- TOC entry 3197 (class 1259 OID 25374)
-- Name: assignment_state_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX assignment_state_idx ON public.assignments USING btree (state);


--
-- TOC entry 3202 (class 1259 OID 25375)
-- Name: category_name_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX category_name_idx ON public.categories USING btree (categoryname);


--
-- TOC entry 3209 (class 1259 OID 25383)
-- Name: request_assignmentid_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX request_assignmentid_idx ON public.requests USING btree (assignmentid);


--
-- TOC entry 3210 (class 1259 OID 25380)
-- Name: request_requestedby_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX request_requestedby_idx ON public.requests USING btree (requestedby);


--
-- TOC entry 3211 (class 1259 OID 25381)
-- Name: request_returneddate_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX request_returneddate_idx ON public.requests USING btree (returneddate);


--
-- TOC entry 3212 (class 1259 OID 25382)
-- Name: request_state_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX request_state_idx ON public.requests USING btree (state);


--
-- TOC entry 3215 (class 1259 OID 25384)
-- Name: role_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX role_idx ON public.roles USING btree (rolename);


--
-- TOC entry 3220 (class 1259 OID 25387)
-- Name: user_location_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX user_location_idx ON public.users USING btree (locationid);


--
-- TOC entry 3221 (class 1259 OID 25385)
-- Name: user_role_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX user_role_idx ON public.users USING btree (roleid);


--
-- TOC entry 3222 (class 1259 OID 25386)
-- Name: user_username_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX user_username_idx ON public.users USING btree (username);


--
-- TOC entry 3232 (class 2606 OID 25427)
-- Name: requests fk1ha6o2qgpjwvu7xftxcfdw0eq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fk1ha6o2qgpjwvu7xftxcfdw0eq FOREIGN KEY (requestedby) REFERENCES public.users(staffcode);


--
-- TOC entry 3227 (class 2606 OID 25402)
-- Name: assignments fk4d7j8tc7abcyqcnuswbv3ammf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fk4d7j8tc7abcyqcnuswbv3ammf FOREIGN KEY (assetcode) REFERENCES public.assets(assetcode);


--
-- TOC entry 3225 (class 2606 OID 25392)
-- Name: assets fkbcv8pcymochk6ntks4kljh377; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT fkbcv8pcymochk6ntks4kljh377 FOREIGN KEY (categorycode) REFERENCES public.categories(categorycode);


--
-- TOC entry 3230 (class 2606 OID 25417)
-- Name: requests fke7a8ien8p1ywu34sk14kuyhp2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fke7a8ien8p1ywu34sk14kuyhp2 FOREIGN KEY (acceptedby) REFERENCES public.users(staffcode);


--
-- TOC entry 3234 (class 2606 OID 25437)
-- Name: users fkgrhs0suhl8cbodxn47xadxp94; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkgrhs0suhl8cbodxn47xadxp94 FOREIGN KEY (roleid) REFERENCES public.roles(roleid);


--
-- TOC entry 3226 (class 2606 OID 25397)
-- Name: assets fkgrljitd2jsng6o7tjkgup5x33; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assets
    ADD CONSTRAINT fkgrljitd2jsng6o7tjkgup5x33 FOREIGN KEY (locationid) REFERENCES public.locations(locationid);


--
-- TOC entry 3229 (class 2606 OID 25412)
-- Name: assignments fkl6l8b6askq6oqrv4ray4gwbxc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fkl6l8b6askq6oqrv4ray4gwbxc FOREIGN KEY (assignedto) REFERENCES public.users(staffcode);


--
-- TOC entry 3228 (class 2606 OID 25407)
-- Name: assignments fkmcdcffgmca59o2hi9fcskwcks; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT fkmcdcffgmca59o2hi9fcskwcks FOREIGN KEY (assignedby) REFERENCES public.users(staffcode);


--
-- TOC entry 3233 (class 2606 OID 25432)
-- Name: users fknbgih6spq8ryw49q0yyyfvee7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fknbgih6spq8ryw49q0yyyfvee7 FOREIGN KEY (locationid) REFERENCES public.locations(locationid);


--
-- TOC entry 3231 (class 2606 OID 25422)
-- Name: requests fkqpyo1upnqnraqpush9k502sph; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT fkqpyo1upnqnraqpush9k502sph FOREIGN KEY (assignmentid) REFERENCES public.assignments(assignmentid);


-- Completed on 2021-12-02 11:42:39

--
-- PostgreSQL database dump complete
--

