# --- !Ups
ALTER TABLE D_FORM_COL ADD FOREIGN KEY
ALTER TABLE D_FORM_COL_SELECT ADD FOREIGN KEY
ALTER TABLE D_FORM_COL_VALIDATION ADD FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK ADD FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_CONDITION ADD FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_MAIL ADD FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE ADD FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE_FIELD ADD FOREIGN KEY

# --- !Downs
ALTER TABLE D_FORM_COL DROP FOREIGN KEY
ALTER TABLE D_FORM_COL_SELECT DROP FOREIGN KEY
ALTER TABLE D_FORM_COL_VALIDATION DROP FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK DROP FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_CONDITION DROP FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_MAIL DROP FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE DROP FOREIGN KEY
ALTER TABLE D_FORM_TRANSFER_TASK_SALESFORCE_FIELD DROP FOREIGN KEY