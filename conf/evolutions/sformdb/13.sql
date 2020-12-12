# --- !Ups
ALTER TABLE d_transfer_tasks
ADD FORM_ID INTEGER GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL AFTER STATUS;

# --- !Downs
ALTER TABLE d_transfer_tasks DROP FORM_ID;