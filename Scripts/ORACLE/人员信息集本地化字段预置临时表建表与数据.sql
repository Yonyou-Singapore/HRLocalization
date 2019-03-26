--drop table hr_infoset_item_sealocal;
create table hr_infoset_item_sealocal (
country VARCHAR(50) NOT NULL,
item_code VARCHAR(50) NOT NULL,
item_name VARCHAR(192),
item_name2 VARCHAR(192),
item_name3 VARCHAR(192),
item_name4 VARCHAR(192),
item_name5 VARCHAR(192),
item_name6 VARCHAR(192),
data_type smallint,
ref_model_name VARCHAR(192),
max_length smallint,
precise smallint,
resid VARCHAR(128),
respath VARCHAR(128),
nullable CHAR(1),
unique_flag CHAR(1)
);

-- Insert Global Fields
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_passportno', N'Passport Number', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000001', N'6007psn', 'Y', 'Y');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_passportissuedate', N'Passport Issue Date', NULL, NULL, NULL, NULL, NULL, 3, NULL, 19, 0, N'hrlocal-000002', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_passportexpirydate', N'Passport Expiry Date', NULL, NULL, NULL, NULL, NULL, 3, NULL, 19, 0, N'hrlocal-000003', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_paymentmode', N'Payment Mode', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL001', 20, 0, N'hrlocal-000004', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_bank', N'Bank Name', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL002', 20, 0, N'hrlocal-000005', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_bankcode', N'Bank Code', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000006', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_bankbranchcode', N'Bank Branch Code', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000007', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'GLOBAL', N'g_bankacno', N'Bank A/C No', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000008', N'6007psn', 'Y', 'Y');

-- Insert Malaysia Fields
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_category', N'Personnel Category', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL003', 20, 0, N'hrlocal-000009', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_entrydate', N'Entry Date', NULL, NULL, NULL, NULL, NULL, 3, NULL, 19, 0, N'hrlocal-000010', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_numberofchildren', N'Number of children', NULL, NULL, NULL, NULL, NULL, 1, NULL, 8, 0, N'hrlocal-000011', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_religion', N'Religion', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL004', 20, 0, N'hrlocal-000012', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_isdisabled', N'Disabled', NULL, NULL, NULL, NULL, NULL, 4, NULL, 1, 0, N'hrlocal-000013', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_isspousedisabled', N'Spouse Disabled', NULL, NULL, NULL, NULL, NULL, 4, NULL, 1, 0, N'hrlocal-000014', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_isspouseworking', N'Spouse Working', NULL, NULL, NULL, NULL, NULL, 4, NULL, 1, 0, N'hrlocal-000015', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_epfgroup', N'EPF Group', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL005', 20, 0, N'hrlocal-000016', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_socsogroup', N'Socso Group', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL007', 20, 0, N'hrlocal-000017', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_eisgroup', N'EIS Group', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL008', 20, 0, N'hrlocal-000018', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_pcbgroup', N'PCB Group', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL006', 20, 0, N'hrlocal-000019', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_epfno', N'EPF No.', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000020', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_socsono', N'Socso No.', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000021', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_taxno', N'Tax No.', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000022', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_taxbranch', N'Tax Branch', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL009', 20, 0, N'hrlocal-000023', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_spousetaxno', N'Spouse Tax No.', NULL, NULL, NULL, NULL, NULL, 0, NULL, 101, 0, N'hrlocal-000024', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_spousetaxbranch', N'Spouse Tax Branch', NULL, NULL, NULL, NULL, NULL, 5, N'SEALOCAL009', 20, 0, N'hrlocal-000025', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totalpayable', N'Total Payable (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000026', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_taxexemption', N'Total Tax Exemption (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000027', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totalepf', N'Total EPF (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000028', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totaleis', N'Total EIS (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000029', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totalsocso', N'Total SOCSO (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000030', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totalzakat', N'Total Zakat (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000031', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_totalpcb', N'Total PCB (previous employer)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000032', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_isvoluntaryepf', N'Is Voluntary EPF', NULL, NULL, NULL, NULL, NULL, 4, NULL, 1, 0, N'hrlocal-000034', N'6007psn', 'Y', 'N');
INSERT INTO hr_infoset_item_sealocal VALUES(N'MY', N'my_nepfrate_employee', N'EPF Rate (Employee)', NULL, NULL, NULL, NULL, NULL, 2, NULL, 28, 8, N'hrlocal-000035', N'6007psn', 'Y', 'N');
insert into hr_infoset_item_sealocal (COUNTRY, ITEM_CODE, ITEM_NAME, ITEM_NAME2, ITEM_NAME3, ITEM_NAME4, ITEM_NAME5, ITEM_NAME6, DATA_TYPE, REF_MODEL_NAME, MAX_LENGTH, PRECISE, RESID, RESPATH, NULLABLE, UNIQUE_FLAG)
values ('MY', 'my_openingdate', 'Openning Date', null, null, null, null, null, 2, null, 28, 8, 'hrlocal-000032', '6007psn', 'N', 'N');

commit;
