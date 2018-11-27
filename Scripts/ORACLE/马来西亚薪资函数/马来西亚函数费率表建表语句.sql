drop table sealocal_epf_rates;
create table sealocal_epf_rates
(
group_name varchar(200),
lower decimal(31,8),
upper decimal(31,8),
employer_cont decimal(31,8),
employee_cont decimal(31,8)
);

drop table my_eis_rates;
create table my_eis_rates (
lower decimal(31,8),
upper decimal(31,8),
employer_cont decimal(31,8),
employee_cont decimal(31,8)
);

drop table my_socso_rates;
create table my_socso_rates (
category_name varchar(200),
lower decimal(31,8),
upper decimal(31,8),
employer_cont decimal(31,8),
employee_cont decimal(31,8)
);
