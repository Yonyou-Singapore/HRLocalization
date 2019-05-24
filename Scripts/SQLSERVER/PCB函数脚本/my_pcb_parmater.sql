 --drop table
 drop table WA_MYPCB_PARAMETER;
 --create table
  CREATE TABLE "WA_MYPCB_PARAMETER" 
   (  "CODE" VARCHAR2(20) DEFAULT '~',
   "NAME" VARCHAR2(50) default '~', 
  "VALUE" NUMBER(28,8), 
  "DESC" VARCHAR2(50) default '~'
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 262144 NEXT 262144 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "NNC_DATA01";
  
  --delete
  delete from WA_MYPCB_PARAMETER where 1=1;
  --insert
  insert into WA_MYPCB_PARAMETER values('TQ', 'Total qualifying amount per year', 6000, '用于校验K2');
  insert into WA_MYPCB_PARAMETER values('D', 'Individual Deductions', 9000, '个人抵扣税额');
  insert into WA_MYPCB_PARAMETER values('S','Spouse Deductions', 4000, '个人抵扣税额');
  insert into WA_MYPCB_PARAMETER values('DU','Disabled Person Deductions', 6000, '个人抵扣税额');
  insert into WA_MYPCB_PARAMETER values('SU', 'Disabled Spouse Deductions', 3500, null);
  insert into WA_MYPCB_PARAMETER values('RATE', 'Non-Resident Tax Rate', 0.28, null);
  insert into WA_MYPCB_PARAMETER values('QC','Deduction for per child', 2000, null);
  commit;