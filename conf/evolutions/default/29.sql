# --- !Ups
ALTER TABLE D_FORM_TRANSFER_TASK
ADD COLUMN NAME VARCHAR(255) NULL AFTER TASK_INDEX;

# --- !Downs
ALTER TABLE D_FORM_TRANSFER_TASK
DROP COLUMN NAME