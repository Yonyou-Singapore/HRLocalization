alter table wa_classitem add (
	SG_INPE CHAR(1), 
	SG_IIR8ACAT VARCHAR2(2), 
	SG_IAPP8ACAT VARCHAR2(2), 
	SG_PRORATE VARCHAR2(2),
	G_FIXEDDAYS NUMBER(28,8),
	SG_AW_OR_OW VARCHAR2(2),
	SG_BRP_OR_GRP VARCHAR2(2)
	);
  
alter table wa_item add (
	SG_INPE CHAR(1), 
	SG_IIR8ACAT VARCHAR2(2), 
	SG_IAPP8ACAT VARCHAR2(2), 
	SG_PRORATE VARCHAR2(2),
	G_FIXEDDAYS NUMBER(28,8),
	SG_AW_OR_OW VARCHAR2(2),
	SG_BRP_OR_GRP VARCHAR2(2)
	);
