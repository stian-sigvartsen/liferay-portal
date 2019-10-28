create index IX_AD3D6F63 on MFAEmailOTP (mfaCheckerName[$COLUMN_LENGTH:75$], userId);
create index IX_9528CD17 on MFAEmailOTP (userId);

create index IX_E9D0CF1B on MFAEmailOTPEntry (userId);