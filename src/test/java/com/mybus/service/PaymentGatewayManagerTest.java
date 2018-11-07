package com.mybus.service;

import com.mybus.controller.AbstractControllerIntegrationTest;
import com.mybus.dao.PaymentGatewayDAO;
import com.mybus.dao.RoleDAO;
import com.mybus.dao.impl.PaymentGatewayMongoDAO;
import com.mybus.model.PaymentGateway;
import com.mybus.model.Role;
import junit.framework.Assert;
import org.apache.commons.collections.IteratorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by HARIPRASADREDDYGURAM on 5/8/2016.
 */
public class PaymentGatewayManagerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PaymentGatewayDAO payGWDAO;

    @Autowired
    private PaymentGatewayMongoDAO payGWMonDAO;
    @Autowired
    private PaymentGatewayManager payGWManager;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        cleanup();
    }

    @After
    public void tearDown() throws Exception {
        cleanup();
    }

    void cleanup(){
        payGWDAO.deleteAll();
    }


    public PaymentGateway createPaymentGatewayObject(){

            PaymentGateway pg = new PaymentGateway();
            pg.setPgKey("eCwWELxi"); //payu  salt
            pg.setPgAccountID("gtKFFx"); //payu key
            pg.setPgRequestUrl("https://test.payu.in/_payment");
            pg.setPaymentType("PG");
            pg.setName("PAYU");
            payGWManager.savePaymentGateway(pg);

        return pg;
    }


    @Test
    public void testSavePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();
        payGWManager.savePaymentGateway(pgData);
        Assert.assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));
        PaymentGateway pg = new PaymentGateway();
        pg.setPgKey("eCwWELxi"); //payu  salt
        pg.setPgAccountID("gtKFFx"); //payu key
        pg.setPgRequestUrl("https://test.payu.in/_payment");
        pg.setPaymentType("PG");
        pg.setName("PAYU");
        payGWDAO.save(pg);
        Assert.assertNotNull(payGWDAO.findById(pg.getId()).get());
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("A payment gateway already exists with same name");
        payGWManager.savePaymentGateway(pg);

    }



    @Test
    public void testDeletePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();

        Assert.assertNotNull(pgData);
        Assert.assertNotNull(pgData.getId());
        payGWManager.deletePaymentGateway(pgData.getId());
        Assert.assertNull(payGWManager.getPaymentGateWayById(pgData.getId()));

    }

    @Test
    public void getPaymentGatewayByID() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();

        Assert.assertNotNull(pgData);
        Assert.assertNotNull(pgData.getId());
        Assert.assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));


    }

    @Test
    public void getPaymentGateways() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();
        Assert.assertNotNull(payGWManager.getPaymentGateWayById(pgData.getId()));
        PaymentGateway pg = new PaymentGateway();
        pg.setPgKey("eCwWELxi1"); //payu  salt
        pg.setPgAccountID("gtKFFxq"); //payu key
        pg.setPgRequestUrl("https://test.mayu.in/_payment");
        pg.setPaymentType("PG1");
        pg.setName("PAYU1");
        payGWManager.savePaymentGateway(pg);
        Assert.assertNotNull(payGWManager.getAllPaymentGateways());
    }

    @Test
    public void getPaymentGatewayByName() throws Exception {
        PaymentGateway pgData = createPaymentGatewayObject();

        Assert.assertNotNull(pgData);
        Assert.assertNotNull(pgData.getId());
        Assert.assertNotNull(payGWManager.getPaymentGateWayByName(pgData.getName()));


    }
}
