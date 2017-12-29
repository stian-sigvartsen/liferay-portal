create index IX_8295BE2C on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], companyId, oAuth2ScopeName[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_BA2C0672 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], companyId, oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_593E3746 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], oAuth2ScopeName[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_D52B0E18 on OAuth2ScopeGrant (applicationName[$COLUMN_LENGTH:75$], bundleSymbolicName[$COLUMN_LENGTH:75$], bundleVersion[$COLUMN_LENGTH:75$], oAuth2TokenId[$COLUMN_LENGTH:75$]);
create index IX_310FAE60 on OAuth2ScopeGrant (oAuth2TokenId[$COLUMN_LENGTH:75$]);

create index IX_E459D81C on OAuth2Token (oAuth2ApplicationId[$COLUMN_LENGTH:75$]);