package com.liferay.saml.admin.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.batch.planner.batch.engine.broker.BatchEngineBroker;
import com.liferay.batch.planner.constants.BatchPlannerPlanConstants;
import com.liferay.batch.planner.model.BatchPlannerPlan;
import com.liferay.batch.planner.service.BatchPlannerMappingService;
import com.liferay.batch.planner.service.BatchPlannerPlanService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.deploy.auto.AutoDeployListener;
import com.liferay.portal.kernel.deploy.auto.context.AutoDeploymentContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@RunWith(Arquillian.class)
public class BatchEngineImportTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		autoCloseables = new ArrayList<>();

		_temporaryClearConfiguration(CompanyConstants.SYSTEM);
		_temporaryClearConfiguration(TestPropsValues.getCompanyId());
	}

	@After
	public void tearDown() {
		_cleanUp();
	}

	private void _cleanUp() {
		ListIterator<AutoCloseable> listIterator = autoCloseables.listIterator(
			autoCloseables.size());

		while (listIterator.hasPrevious()) {
			AutoCloseable previousAutoCloseable = listIterator.previous();

			try {
				previousAutoCloseable.close();
			}
			catch (Exception exception) {
				//_log.error(exception);
			}
		}
	}

	protected ArrayList<AutoCloseable> autoCloseables;

//	@Test
//	public void testCreateBlogPostingsFromInvalidJSONFile() throws Exception {
//		StringBundler sb = new StringBundler();
//
//		sb.append(StringPool.OPEN_BRACKET);
//
////		_createJSONRow(
////			sb, FIELD_NAMES[0], _toJSONValue("alternativeHeadline"),
////			FIELD_NAMES[1], _toJSONValue("articleBody"), FIELD_NAMES[2],
////			_toJSONValue(dateFormat.format(new Date(baseDate.getTime()))),
////			FIELD_NAMES[3], _toJSONValue("headline"), FIELD_NAMES[4],
////			String.valueOf(group.getGroupId()), "unknownColumn",
////			_toJSONValue("unknownValue"));
//
//		sb.append(StringPool.CLOSE_BRACKET);
//
//		String content = sb.toString();
//
//		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
//			_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
//			LoggerTestUtil.ERROR)) {
//
//			_importBlogPostings(
//				BatchEngineTaskOperation.CREATE,
//				_compressContent(
//					content.getBytes(StandardCharsets.UTF_8), "JSON"),
//				"JSON", null);
//
//			_assertInvalidFileImportWithOnErrorFailStrategy(1, 1, logCapture);
//		}
//	}

	@Test
	public void testFileImport()
		throws PortalException, ExecutionException, InterruptedException,
		TimeoutException, IOException {

//	private void _deploy(ZipFile zipFile) throws Exception {
//		BatchEngineZipEntryPair batchEngineZipEntryPair =
//			_getBatchEngineZipEntryPair(zipFile);
//
//		BatchEngineImportConfiguration batchEngineImportConfiguration = null;
		byte[] content = null;
		String contentType = null;

//		if (batchEngineZipEntryPair.isValid()) {
//			try (InputStream inputStream = zipFile.getInputStream(
//				batchEngineZipEntryPair._configurationZipEntry)) {
//
//				batchEngineImportConfiguration = _objectMapper.readValue(
//					inputStream, BatchEngineImportConfiguration.class);
//			}

//			UnsyncByteArrayOutputStream compressedUnsyncByteArrayOutputStream =
//				new UnsyncByteArrayOutputStream();
//
//			ZipEntry dataZipEntry = batchEngineZipEntryPair._dataZipEntry;
//
//			try (InputStream inputStream = zipFile.getInputStream(dataZipEntry);
//				 ZipOutputStream zipOutputStream = new ZipOutputStream(
//					 compressedUnsyncByteArrayOutputStream)) {
//
//				zipOutputStream.putNextEntry(
//					new ZipEntry(dataZipEntry.getName()));
//
//				StreamUtil.transfer(inputStream, zipOutputStream, false);
//			}
//
//			content = compressedUnsyncByteArrayOutputStream.toByteArray();

			String json = "[{\n" +
						  "  \"enabled\" : true,\n" +
						  "  \"entityId\" : \"liferaysamlsp\",\n" +
						  "  \"role\" : \"sp\",\n" +
						  "  \"signMetadata\" : true,\n" +
						  "  \"sp\" : {\n" +
						  "    \"allowShowingTheLoginPortlet\" : true,\n" +
						  "    \"assertionSignatureRequired\" : false,\n" +
						  "    \"clockSkew\" : 3000,\n" +
						  "    \"idpConnections\" : [ {\n" +
						  "      \"assertionSignatureRequired\" : false,\n" +
						  "      \"clockSkew\" : 3000,\n" +
						  "      \"enabled\" : true,\n" +
						  "      \"entityId\" : \"liferaysamlidp\",\n" +
						  "      \"forceAuthn\" : false,\n" +
						  "      \"id\" : 1,\n" +
						  "      \"metadataUpdatedDate\" : \"2022-06-21T10:30:48Z\",\n" +
						  "      \"metadataUrl\" : \"http://liferaysamlidp.com:8080/c/portal/saml/metadata\",\n" +
						  "      \"name\" : \"liferaysamlidp.com\",\n" +
						  "      \"nameIdFormat\" : \"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\",\n" +
						  "      \"signAuthnRequest\" : false,\n" +
						  "      \"unknownUsersAreStrangers\" : false,\n" +
						  "      \"userAttributeMappings\" : \"#Tue Jun 21 10:30:48 GMT 2022\\n\"\n" +
						  "    } ],\n" +
						  "    \"ldapImportEnabled\" : false,\n" +
						  "    \"signAuthnRequest\" : true\n" +
						  "  },\n" +
						  "  \"sslRequired\" : false\n" +
						  "}]";

			UnsyncByteArrayOutputStream compressedUnsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();

			try (ZipOutputStream zipOutputStream = new ZipOutputStream(
					compressedUnsyncByteArrayOutputStream)) {

				zipOutputStream.putNextEntry(
					new ZipEntry("testdata.json"));

				zipOutputStream.write(json.getBytes(Charset.defaultCharset()));
				//StreamUtil.transfer(inputStream, zipOutputStream, false);
			}

			content = compressedUnsyncByteArrayOutputStream.toByteArray();

			contentType = "json"; //_file.getExtension(dataZipEntry.getName());
//		}

//		if ((batchEngineImportConfiguration == null) || (content == null) ||
//			Validator.isNull(contentType)) {
//
//			throw new IllegalStateException(
//				"Invalid batch engine file " + zipFile.getName());
//		}

		ExecutorService executorService =
			_portalExecutorManager.getPortalExecutor(
				BatchEngineImportTest.class.getName()); //BatchEngineAutoDeployListener.class.getName());


		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null,
				TestPropsValues.getCompanyId(), //batchEngineImportConfiguration.companyId,
				TestPropsValues.getUserId(), //batchEngineImportConfiguration.userId,
				100,
				null, //batchEngineImportConfiguration.callbackURL,
				"com.liferay.saml.admin.rest.dto.v1_0.SamlProvider", //batchEngineImportConfiguration.className,
				content,
				StringUtil.toUpperCase(contentType),
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				null, //batchEngineImportConfiguration.fieldNameMappingMap,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(),
				null, // batchEngineImportConfiguration.parameters,
				null); //batchEngineImportConfiguration.taskItemDelegateName);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
			_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
			LoggerTestUtil.FATAL)) {

			_batchEngineImportTaskExecutor.execute(batchEngineImportTask);
		}

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.FAILED.name(),
			batchEngineImportTask.getExecuteStatus());

		Assert.assertEquals(
			"com.liferay.saml.runtime.exception.CredentialAuthException: CREDENTIAL_PASSWORD_INCORRECT",
			batchEngineImportTask.getErrorMessage());

	}

	protected AutoDeploymentContext buildAutoDeploymentContext(File file) {
		AutoDeploymentContext autoDeploymentContext =
			new AutoDeploymentContext();

		autoDeploymentContext.setFile(file);

		return autoDeploymentContext;
	}

	private BatchPlannerPlan _submitBatchPlannerPlan(
		boolean export, String internalClassName, String name)
		throws Exception {

		BatchPlannerPlan batchPlannerPlan =
			_batchPlannerPlanService.addBatchPlannerPlan(
				export, BatchPlannerPlanConstants.EXTERNAL_TYPE_CSV,
				"/" + RandomTestUtil.randomString(), internalClassName, name, 0,
				null, false);

		_batchPlannerMappingService.addBatchPlannerMapping(
			batchPlannerPlan.getBatchPlannerPlanId(), "name", "String", "name",
			"String", StringPool.BLANK);

		try {
			_batchEngineBroker.submit(batchPlannerPlan.getBatchPlannerPlanId());
		}
		catch (Exception exception) {
			Assert.assertEquals(
				IllegalArgumentException.class, exception.getClass());
		}

		return batchPlannerPlan;
	}

	private byte[] _compressContent(byte[] content, String contentType)
		throws Exception {

		try (ByteArrayOutputStream byteArrayOutputStream =
				 new ByteArrayOutputStream()) {

			try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				byteArrayOutputStream)) {

				ZipEntry zipEntry = new ZipEntry(
					"import." + StringUtil.toLowerCase(contentType));

				zipOutputStream.putNextEntry(zipEntry);

				zipOutputStream.write(content, 0, content.length);
			}

			return byteArrayOutputStream.toByteArray();
		}
	}

	private void _deleteSamlProviderConfiguration(long companyId)
		throws Exception {

		Configuration configuration = _getSamlProviderConfiguration(companyId);

		if (configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(configuration);
		}
	}

	private Configuration _getSamlProviderConfiguration(long companyId)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(&(service.factoryPid=com.liferay.saml.runtime.configuration." +
			"SamlProviderConfiguration)(companyId=" + companyId + "))");

		if ((configurations != null) && (configurations.length > 0)) {
			return configurations[0];
		}

		return null;
	}

	private void _temporaryClearConfiguration(long companyId) throws Exception {
		Configuration configuration = _getSamlProviderConfiguration(companyId);

		if (configuration != null) {
			Dictionary<String, Object> dictionary =
				configuration.getProperties();

			dictionary.put("companyId", -companyId);

			ConfigurationTestUtil.saveConfiguration(configuration, dictionary);

			autoCloseables.add(
				() -> {
					_deleteSamlProviderConfiguration(companyId);
					dictionary.put("companyId", companyId);

					ConfigurationTestUtil.saveConfiguration(
						configuration, dictionary);
				});
		}
		else {
			autoCloseables.add(
				() -> _deleteSamlProviderConfiguration(companyId));
		}
	}

//	private void _assertInvalidFileImportWithOnErrorFailStrategy(
//		int invalidItemRowNumber, int itemsCount, LogCapture logCapture) {
//
//		Assert.assertEquals(
//			BatchEngineTaskExecuteStatus.FAILED.toString(),
//			_batchEngineImportTask.getExecuteStatus());
//		Assert.assertEquals(0, _batchEngineImportTask.getProcessedItemsCount());
//		Assert.assertEquals(
//			itemsCount, _batchEngineImportTask.getTotalItemsCount());
//
//		List<BatchEngineImportTaskError> batchEngineImportTaskErrors =
//			_batchEngineImportTaskErrorLocalService.
//				getBatchEngineImportTaskErrors(
//					_batchEngineImportTask.getBatchEngineImportTaskId());
//
//		Assert.assertEquals(
//			batchEngineImportTaskErrors.toString(), 1,
//			batchEngineImportTaskErrors.size());
//
//		BatchEngineImportTaskError batchEngineImportTaskError =
//			batchEngineImportTaskErrors.get(0);
//
//		Assert.assertEquals(
//			invalidItemRowNumber, batchEngineImportTaskError.getItemIndex());
//
//		Assert.assertEquals(
//			0,
//			blogsEntryLocalService.getGroupEntriesCount(
//				group.getGroupId(),
//				new QueryDefinition<>(WorkflowConstants.STATUS_APPROVED)));
//
//		List<LogEntry> logEntries = logCapture.getLogEntries();
//
//		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());
//
//		LogEntry logEntry = logEntries.get(0);
//
//		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());
//
//		String message = logEntry.getMessage();
//
//		Assert.assertTrue(
//			message.startsWith("Unable to update batch engine import task"));
//	}

//	private void _importBlogPostings(
//		BatchEngineTaskOperation batchEngineTaskOperation, byte[] content,
//		String contentType, Map<String, String> fieldNameMappingMap)
//		throws Exception {
//
//		_importBlogPostings(
//			batchEngineTaskOperation, content, contentType, fieldNameMappingMap,
//			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL);
//	}

//	private void _importBlogPostings(
//		BatchEngineTaskOperation batchEngineTaskOperation, byte[] content,
//		String contentType, Map<String, String> fieldNameMappingMap,
//		int importStrategy)
//		throws Exception {
//
//		Map<String, Serializable> parameters = Collections.emptyMap();
//
//		if (batchEngineTaskOperation == BatchEngineTaskOperation.CREATE) {
//			parameters = HashMapBuilder.<String, Serializable>put(
//				"siteId", (Serializable)String.valueOf(group.getGroupId())
//			).build();
//		}
//
//		_batchEngineImportTask =
//			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
//				null, group.getCompanyId(), user.getUserId(), _BATCH_SIZE, null,
//				BlogPosting.class.getName(), content, contentType,
//				BatchEngineTaskExecuteStatus.INITIAL.name(),
//				fieldNameMappingMap, importStrategy,
//				batchEngineTaskOperation.name(), parameters, null);
//
//		_batchEngineImportTaskExecutor.execute(_batchEngineImportTask);
//	}

//	@DeleteAfterTestRun
	private BatchEngineImportTask _batchEngineImportTask;

//	@Inject
	private BatchEngineBroker _batchEngineBroker;

//	@Inject
	private BatchPlannerMappingService _batchPlannerMappingService;

//	@Inject
	private BatchPlannerPlanService _batchPlannerPlanService;

	@Inject
	private BatchEngineImportTaskLocalService _batchEngineImportTaskLocalService;

	@Inject
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Inject
	private PortalExecutorManager _portalExecutorManager;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

//	@Inject (filter="(component.name=com.liferay.batch.engine.internal.auto.deploy.BatchEngineAutoDeployListener)")
//	private AutoDeployListener _autoDeployListener;

	private static final String
		_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL =
			"com.liferay.batch.engine.internal." +
				"BatchEngineImportTaskExecutorImpl";
}
