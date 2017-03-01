package com.mybus.service;

import com.mybus.dao.AgentDAO;
import com.mybus.model.Agent;
import com.mybus.model.ServiceReport;
import com.mybus.model.ServiceReportStatus;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;


/**
 * Created by srinikandula on 2/18/17.
 */
@Service
public class ABAgentService {
    private static final String ABHI_BUS_URL = "http://api.abhibus.com/abhibusoperators/srikrishna/server.php?SecurityKey=SRI*FDEU!@@%ANHSIRK";
    public static XmlRpcClient xmlRpcClient;
    private static final Logger logger = LoggerFactory.getLogger(ABAgentService.class);

    @Autowired
    private AgentDAO agentDAO;

    public void init() {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(ABHI_BUS_URL));
            xmlRpcClient = new XmlRpcClient();
            xmlRpcClient.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlRpcClient));
            xmlRpcClient.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadAgents() throws Exception{
        logger.info("downloading agents data:" );
        init();
        Collection<Agent> agents = new ArrayList<>();
        Vector params = new Vector();
        Object infos[] = (Object[]) xmlRpcClient.execute("index.agentdetails", params);
        for (Object a: infos) {
            Map info = (HashMap) a;
            if(info.get("status").toString().equalsIgnoreCase("active")){
                String userName = info.get("username").toString();
                if(agentDAO.findByUsername(userName).iterator().hasNext()){
                    logger.debug("Skipping downloading of existing agent: "+ userName);
                    continue;
                }
                Agent agent = new Agent();
                if(info.containsKey("username")){
                    agent.setUsername(info.get("username").toString());
                }
                if(info.containsKey("firstname")){
                    agent.setFirstname(info.get("firstname").toString());
                }
                if(info.containsKey("lastname")){
                    agent.setLastname(info.get("lastname").toString());
                }
                if(info.containsKey("email")){
                    agent.setEmail(info.get("email").toString());
                }
                if(info.containsKey("mobile")){
                    agent.setMobile(info.get("mobile").toString());
                }
                if(info.containsKey("landline")){
                    agent.setLandline(info.get("landline").toString());
                }
                if(info.containsKey("address")){
                    agent.setAddress(info.get("address").toString());
                }
                if(info.containsKey("branchname")){
                    agent.setBranchName(info.get("branchname").toString());
                }
                agent.setActive(true);
                agents.add(agent);
            }
        }
        agentDAO.save(agents);
    }
    public static void main(String args[]) {
        try {
            new ABAgentService().downloadAgents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}