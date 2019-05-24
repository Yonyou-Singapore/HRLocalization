--  银行报盘添加了第二个标志行
alter table hr_dataio_intface add iiftop2 INT NULL;
alter table hr_dataio_intface add toplineposition2 INT NULL;
alter table hr_dataio_intface add toplinenum2 INT NULL;

-- 银行报盘添加折行选项和日期格式
alter table hr_dataintface_b add inextline INT NULL;
alter table hr_dataintface_b add dateformat VARCHAR(101);

-- 标志行VO添加折行选项并标记是首行还是尾行
alter table hr_ifsettop add inextline INT NULL;
alter table hr_ifsettop add itoplineposition INT NULL;

-- 2019-03-18新添加字段
alter table hr_dataintface_b add iskipifzero INT NULL;