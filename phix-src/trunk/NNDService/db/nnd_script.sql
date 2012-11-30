-- Creates sample LOINC/SNOMED combinations for notifiable conditions.

-- Run against hub database on target machine to create entries
-- and new tables required for NNDService.


insert into organization values (nextval('organization_seq'), 'PHIX', 'PHIX', 'unused@unused.xyz',  'notify@unused.xyz', 'unused', 'XDR', 'PHIX', 'notify@unused.xyz');


insert into organization values (nextval('organization_seq'), 'ATLAS', 'ATLAS', 'unused@unused.xyz',  'notify@unused.xyz', 'unused', 'XDR', 'ATLAS', 'notify@unused.xyz');


insert into component_routing values (nextval('component_routing_seq'), 'CDA', 'NONE', 'v3', (select id from organization where facility='PHIX'), (select id from organization where facility='ATLAS'), 'f','f','f','t','f','f','f',null,null,null,'',null);

-- new table: 
create sequence nnd_seq;

CREATE TABLE nnd
(
  id bigint NOT NULL,
  testcode character varying(100) NOT NULL,
  resultcode character varying(100) NOT NULL,

  CONSTRAINT pk_nnd_id PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organization OWNER TO postgres;

insert into nnd values (nextval('nnd_seq'), '38379-4', '33610009');
insert into nnd values (nextval('nnd_seq'), '38379-4', '51320008');
insert into nnd values (nextval('nnd_seq'), '38379-4', '27142009');
insert into nnd values (nextval('nnd_seq'), '38379-4', '414789006');
insert into nnd values (nextval('nnd_seq'), '38379-4', '430579009');
insert into nnd values (nextval('nnd_seq'), '38379-4', '70801007');
insert into nnd values (nextval('nnd_seq'), '38379-4', '430914003');
insert into nnd values (nextval('nnd_seq'), '38379-4', '113861009');
insert into nnd values (nextval('nnd_seq'), '38379-4', '243372002');
insert into nnd values (nextval('nnd_seq'), '38379-4', '243373007');
insert into nnd values (nextval('nnd_seq'), '38379-4', '243371009');
insert into nnd values (nextval('nnd_seq'), '38379-4', '243370005');
insert into nnd values (nextval('nnd_seq'), '38379-4', '113858008');
insert into nnd values (nextval('nnd_seq'), '38379-4', '36354002');
