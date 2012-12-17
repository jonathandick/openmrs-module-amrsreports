package org.openmrs.module.amrsreport.rule.collection;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * Test file for MohUniquePatientNumberRule class. Should check and return CCC Number for a patient
 */
public class MohUniquePatientNumberRuleTest extends BaseModuleContextSensitiveTest {


    /**
     * @verifies return a Unique Patient Number
     * @see MohUniquePatientNumberRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnAUniquePatientNumber() throws Exception {
        MohUniquePatientNumberRule mohUniquePatientNumberRule = new MohUniquePatientNumberRule();
        PatientService  patientService = Context.getPatientService();
        /*Test with a patient with no CCC Number*/

        Patient patient = patientService.getPatient(8);


        Result result = mohUniquePatientNumberRule.evaluate(null, patient.getId(), null);

        Result expectedResult = new Result("not found");

        Assert.assertEquals("The patient has no CCC Number",expectedResult,result );

    }
    /**
     * @should return a valid CCC Number for a patient
     * @verifies return a Unique Patient Number
     * @see MohUniquePatientNumberRule#evaluate(org.openmrs.logic.LogicContext, Integer, java.util.Map)
     */
    @Test
    public void evaluate_shouldReturnAvalidCCCNumber() throws Exception{

        MohUniquePatientNumberRule mohUniquePatientNumberRule = new MohUniquePatientNumberRule();
        PatientService  patientService = Context.getPatientService();
        /*Create a patient and assign a CCC Number*/


        Patient patient1 = patientService.getPatient(8);

        AdministrationService ams = Context.getAdministrationService();

        PatientIdentifierType cccnotype = new PatientIdentifierType();
        cccnotype.setName("CCC Number");
        cccnotype.setDescription("A unique number generated by clinics");
        patientService.savePatientIdentifierType(cccnotype);


        GlobalProperty globalProperty = new GlobalProperty();

        globalProperty.setProperty("cccgenerator.CCC");

        globalProperty.setDescription("A unique patient Number generated by Clinics");
        globalProperty.setPropertyValue("CCC Number");
        ams.saveGlobalProperty(globalProperty);


        Context.flushSession();

        String savedProperty = ams.getGlobalProperty("cccgenerator.CCC");

        PatientIdentifierType pit =  MohCacheUtils.getPatientIdentifierType(Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC"));

        Location location = Context.getLocationService().getLocation(1);

        PatientIdentifier pi = new PatientIdentifier("11740-00001", pit, location);
        patient1.addIdentifier(pi);


        patientService.savePatient(patient1);
        Context.flushSession();

        Result result1 = mohUniquePatientNumberRule.evaluate(null, patient1.getId(), null);

        Result  expectedResult1 = new Result("11740-00001");
        Assert.assertEquals("The two identifiers are not equal",expectedResult1.toString(),result1.toString());
    }
}
