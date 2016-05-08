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


    public PaymentGateway createPaymentGateway(){

            PaymentGateway pg = new PaymentGateway();
            pg.setPgKey("eCwWELxi"); //payu  salt
            pg.setPgAccountID("gtKFFx"); //payu key
            pg.setPgRequestUrl("https://test.payu.in/_payment");
            pg.setPaymentType("PG");
            pg.setName("PAYU");
            payGWDAO.save(pg);

      /*  payuGateway = paymentGatewayDAO.findByName("EBS");
        if(payuGateway == null) {
            PaymentGateway pg = new PaymentGateway();
            pg.setPgKey("ebs key"); //ebs secret key
            pg.setPgAccountID("gtKFFx"); //ebs accountId
            pg.setPgRequestUrl("https://secure.ebs.in/pg/ma/payment/request");
            pg.setPaymentType("PG");
            pg.setUserName("EBS");
            paymentGatewayDAO.save(pg);
        }*/
        return pg;
    }


    @Test
    public void testSavePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGateway();

        Assert.assertNotNull(payGWDAO.findOne(pgData.getId()));
       // expectedEx.expect(RuntimeException.class);
        //expectedEx.expectMessage("Role already exists with this same name");
       // payGWManager.savePaymentGateway(duplicatePgData);
    }

    /*  @Test
     public void testUpdateRole() throws Exception {
        PaymentGateway pgData = createPaymentGateway();
        Role role1 = roleManager.saveRole(new Role("test1"));
        // roleDAO.findOne(role.getId());
        ArrayList roles = (ArrayList) IteratorUtils.toList(roleDAO.findAll().iterator());
        Assert.assertEquals(2, roles.size());
        // role.setName("kee");
        role1.setName("test");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Role already exists with this same name");
        roleManager.saveRole(role1);
        //roleDAO.save(role1);
        Assert.assertEquals("test", role.getName());
    }*/

    @Test
    public void testDeletePaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGateway();
        Assert.assertNotNull(pgData);
        Assert.assertNotNull(pgData.getId());
        payGWDAO.delete(pgData);
        Assert.assertNull(payGWDAO.findOne(pgData.getId()));

    }

    @Test
    public void getPaymentGateway() throws Exception {
        PaymentGateway pgData = createPaymentGateway();
        Assert.assertNotNull(pgData);
        Assert.assertNotNull(pgData.getId());
        payGWDAO.findOne(pgData.getId());
        Assert.assertNotNull(payGWDAO.findOne(pgData.getId()));


    }
}
