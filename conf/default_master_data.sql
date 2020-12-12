insert into m_transfers values(1, 'Salesforce', 'Salesforce', 1, "Admin", Now(), "Admin", Now());
insert into m_transfers values(2, 'Mail', 'Mail', 1, "Admin", Now(), "Admin", Now());
insert into d_transfer_config values(1, 'Salesforce', 0, 'Salesforce転送', 1, 'Admin', 'Admin', 'Admin', Now(), Now());
insert into d_transfer_config values(2, 'Mail', 1, 'Mail転送', 1, 'Admin', 'Admin', 'Admin', Now(), Now());
insert into d_transfer_config_salesforce values(1, 1, 'hoge', 'hoge', 'hoge', 'Admin', 'Admin', 'Admin', Now(), Now());
insert into d_transfer_config_mail values(1, 2, 0, 0, 0, 'Admin', 'Admin', 'Admin', Now(), Now());