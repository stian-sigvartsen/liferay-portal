create table OAuth2Application (
	oAuth2ApplicationId VARCHAR(75) not null primary key,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	oAuth2ApplicationSecret VARCHAR(75) null,
	name VARCHAR(75) null,
	description VARCHAR(75) null
);

create table OAuth2ScopeGrant (
	applicationName VARCHAR(75) not null,
	bundleSymbolicName VARCHAR(75) not null,
	bundleVersion VARCHAR(75) not null,
	companyId LONG not null,
	oAuth2ScopeName VARCHAR(75) not null,
	oAuth2TokenId VARCHAR(75) not null,
	createDate DATE null,
	primary key (applicationName, bundleSymbolicName, bundleVersion, companyId, oAuth2ScopeName, oAuth2TokenId)
);

create table OAuth2Token (
	oAuth2TokenId VARCHAR(75) not null primary key,
	companyId LONG,
	createDate DATE null,
	oAuth2ApplicationId VARCHAR(75) null
);