package io.snice.codecs.codegen;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClassNameConverterTest {

    private ClassNameConverter converter;

    @Before
    public void setUp() {
        converter = ClassNameConverter.defaultConverter();
    }

    /**
     * There are a few exceptions with the GTPv2 Information Elements.
     */
    @Test
    public void testConvertToEnumGtpv2() {
        ensureEnumConversion("P_TMSI", "P-TMSI");

        ensureEnumConversion("RESERVED", "Reserved");
        ensureEnumConversion("IMSI", "International Mobile Subscriber Identity (IMSI)");
        ensureEnumConversion("CAUSE", "Cause");

        // another exception
        ensureEnumConversion("RECOVERY", "Recovery (Restart Counter)");
        ensureEnumConversion("STN_SR", "STN-SR");
        ensureEnumConversion("APN", "Access Point Name (APN)");
        ensureEnumConversion("AMBR", "Aggregate Maximum Bit Rate (AMBR)");
        ensureEnumConversion("EBI", "EPS Bearer ID (EBI)");
        ensureEnumConversion("IP_ADDRESS", "IP Address");
        ensureEnumConversion("MEI", "Mobile Equipment Identity (MEI)");
        ensureEnumConversion("MSISDN", "MSISDN");
        ensureEnumConversion("INDICATION", "Indication");
        ensureEnumConversion("PCO", "Protocol Configuration Options (PCO)");
        ensureEnumConversion("PAA", "PDN Address Allocation (PAA)");
        ensureEnumConversion("BEARER_QOS", "Bearer LevelQuality of Service (Bearer QoS)");
        ensureEnumConversion("FLOW_QOS", "FlowQuality of Service (Flow QoS)");
        ensureEnumConversion("RAT", "RAT Type");
        ensureEnumConversion("SERVING_NETWORK", "Serving Network");
        ensureEnumConversion("BEARER_TFT", "EPS Bearer LevelTraffic Flow Template (Bearer TFT)");
        ensureEnumConversion("TAD", "Traffic Aggregation Description (TAD)");
        ensureEnumConversion("ULI", "User Location Information (ULI)");

        ensureEnumConversion("F_TEID", "Fully Qualified Tunnel Endpoint Identifier (F-TEID)");

        ensureEnumConversion("TMSI", "TMSI");
        ensureEnumConversion("GLOBAL_CN_ID", "Global CN-Id");
        ensureEnumConversion("S103PDF", "S103 PDN Data Forwarding Info (S103PDF)");
        ensureEnumConversion("S1UDF", "S1-U Data Forwarding Info (S1UDF)");
        ensureEnumConversion("DELAY_VALUE", "Delay Value");
        ensureEnumConversion("BEARER_CONTEXT", "Bearer Context");
        ensureEnumConversion("CHARGING_ID", "Charging ID");
        ensureEnumConversion("CHARGING_CHARACTERISTICS", "Charging Characteristics");
        ensureEnumConversion("TRACE_INFORMATION", "Trace Information");
        ensureEnumConversion("BEARER_FLAGS", "Bearer Flags");
        ensureEnumConversion("PDN", "PDN Type");
        ensureEnumConversion("PROCEDURE_TRANSACTION_ID", "Procedure Transaction ID");

        // These are all exceptions to the normal conversion rule.
        ensureEnumConversion("MM_CONTEXT_GSM_KEY_TRIPLETS", "MM Context (GSM Key and Triplets)");
        ensureEnumConversion("MM_CONTEXT_GSM_KEY_TRIPLETS", "MM Context (GSM Key and Triplets)");
        ensureEnumConversion("MM_CONTEXT_UMTS_KEY_USED_CIPHER_QUINTUPLETS", "MM Context (UMTS Key, Used Cipher and Quintuplets)");
        ensureEnumConversion("MM_CONTEXT_GSM_KEY_USED_CIPHER_QUINTUPLETS", "MM Context (GSM Key,Used Cipher and Quintuplets)");
        ensureEnumConversion("MM_CONTEXT_UMTS_KEY_QUINTUPLETS", "MM Context (UMTS Key and Quintuplets)");
        ensureEnumConversion("MM_CONTEXT_EPS_SECURITY_CONTEXT_QUADRUPLETS_QUINTUPLETS", "MM Context (EPS Security Context,Quadruplets and Quintuplets)");
        ensureEnumConversion("MM_CONTEXT_UMTS_KEY_QUADRUPLETS_QUINTUPLETS", "MM Context (UMTS Key, Quadruplets and Quintuplets)");


        ensureEnumConversion("PDN_CONNECTION", "PDN Connection");
        ensureEnumConversion("PDU_NUMBERS", "PDU Numbers");
        ensureEnumConversion("P_TMSI", "P-TMSI");
        ensureEnumConversion("P_TMSI_SIGNATURE", "P-TMSI Signature");

        ensureEnumConversion("HOP_COUNTER", "Hop Counter");
        ensureEnumConversion("UE_TIME_ZONE", "UE Time Zone");
        ensureEnumConversion("TRACE_REFERENCE", "Trace Reference");
        ensureEnumConversion("COMPLETE_REQUEST_MESSAGE", "Complete Request Message");
        ensureEnumConversion("GUTI", "GUTI");
        ensureEnumConversion("F_CONTAINER", "F-Container");
        ensureEnumConversion("F_CAUSE", "F-Cause");
        ensureEnumConversion("PLMN_ID", "PLMN ID");
        ensureEnumConversion("TARGET_IDENTIFICATION", "Target Identification");
        ensureEnumConversion("PACKET_FLOW_ID", "Packet Flow ID");
        ensureEnumConversion("RAB_CONTEXT", "RAB Context");
        ensureEnumConversion("SOURCE_RNC_PDCP_CONTEXT_INFO", "Source RNC PDCP Context Info");
        ensureEnumConversion("PORT_NUMBER", "Port Number");
        ensureEnumConversion("APN_RESTRICTION", "APN Restriction");
        ensureEnumConversion("SELECTION_MODE", "Selection Mode");
        ensureEnumConversion("SOURCE_IDENTIFICATION", "Source Identification");
        ensureEnumConversion("CHANGE_REPORTING_ACTION", "Change Reporting Action");
        ensureEnumConversion("FQ_CSID", "Fully Qualified PDN Connection Set Identifier (FQ-CSID)");
        ensureEnumConversion("CHANNEL_NEEDED", "Channel needed");
        ensureEnumConversion("EMLPP_PRIORITY", "eMLPP Priority");
        ensureEnumConversion("NODE_TYPE", "Node Type");
        ensureEnumConversion("FQDN", "Fully Qualified Domain Name (FQDN)");
        ensureEnumConversion("TI", "Transaction Identifier (TI)");
        ensureEnumConversion("MBMS_SESSION_DURATION", "MBMS Session Duration");
        ensureEnumConversion("MBMS_SERVICE_AREA", "MBMS Service Area");
        ensureEnumConversion("MBMS_SESSION_IDENTIFIER", "MBMS Session Identifier");
        ensureEnumConversion("MBMS_FLOW_IDENTIFIER", "MBMS Flow Identifier");
        ensureEnumConversion("MBMS_IP_MULTICAST_DISTRIBUTION", "MBMS IP Multicast Distribution");
        ensureEnumConversion("MBMS_DISTRIBUTION_ACKNOWLEDGE", "MBMS Distribution Acknowledge");
        ensureEnumConversion("RFSP_INDEX", "RFSP Index");
        ensureEnumConversion("UCI", "User CSG Information (UCI)");
        ensureEnumConversion("CSG_INFORMATION_REPORTING_ACTION", "CSG Information Reporting Action");
        ensureEnumConversion("CSG_ID", "CSG ID");
        ensureEnumConversion("CMI", "CSG Membership Indication (CMI)");
        ensureEnumConversion("SERVICE_INDICATOR", "Service indicator");
        ensureEnumConversion("DETACH_TYPE", "Detach Type");
        ensureEnumConversion("LDN", "Local Distiguished Name (LDN)");
        ensureEnumConversion("NODE_FEATURES", "Node Features");
        ensureEnumConversion("MBMS_TIME_TO_DATA_TRANSFER", "MBMS Time to Data Transfer");
        ensureEnumConversion("THROTTLING", "Throttling");
        ensureEnumConversion("ARP", "Allocation/Retention Priority (ARP)");
        ensureEnumConversion("EPC_TIMER", "EPC Timer");
        ensureEnumConversion("SIGNALLING_PRIORITY_INDICATION", "Signalling Priority Indication");
        ensureEnumConversion("TMGI", "Temporary Mobile Group Identity (TMGI)");
        ensureEnumConversion("ADDITIONAL_MM_CONTEXT_FOR_SRVCC", "Additional MM context for SRVCC");
        ensureEnumConversion("ADDITIONAL_FLAGS_FOR_SRVCC", "Additional flags for SRVCC");
        ensureEnumConversion("MDT_CONFIGURATION", "MDT Configuration");
        ensureEnumConversion("APCO", "Additional Protocol Configuration Options (APCO)");
        ensureEnumConversion("ABSOLUTE_TIME_OF_MBMS_DATA_TRANSFER", "Absolute Time of MBMS Data Transfer");
        ensureEnumConversion("HENB_INFORMATION_REPORTING", "H(e)NB Information Reporting");
        ensureEnumConversion("IP4CP", "IPv4 Configuration Parameters (IP4CP)");
        ensureEnumConversion("CHANGE_TO_REPORT_FLAGS", "Change to Report Flags");
        ensureEnumConversion("ACTION_INDICATION", "Action Indication");
        ensureEnumConversion("TWAN_IDENTIFIER", "TWAN Identifier");
        ensureEnumConversion("ULI_TIMESTAMP", "ULI Timestamp");
        ensureEnumConversion("MBMS_FLAGS", "MBMS Flags");
        ensureEnumConversion("RAN_NAS_CAUSE", "RAN/NAS Cause");
        ensureEnumConversion("CN_OPERATOR_SELECTION_ENTITY", "CN Operator Selection Entity");
        ensureEnumConversion("TRUSTED_WLAN_MODE_INDICATION", "Trusted WLAN Mode Indication");
        ensureEnumConversion("NODE_NUMBER", "Node Number");
        ensureEnumConversion("NODE_IDENTIFIER", "Node Identifier");
        ensureEnumConversion("PRESENCE_REPORTING_AREA_ACTION", "Presence Reporting Area Action");
        ensureEnumConversion("PRESENCE_REPORTING_AREA_INFORMATION", "Presence Reporting Area Information");
        ensureEnumConversion("TWAN_IDENTIFIER_TIMESTAMP", "TWAN Identifier Timestamp");
        ensureEnumConversion("OVERLOAD_CONTROL_INFORMATION", "Overload Control Information");
        ensureEnumConversion("LOAD_CONTROL_INFORMATION", "Load Control Information");
        ensureEnumConversion("METRIC", "Metric");
        ensureEnumConversion("SEQUENCE_NUMBER", "Sequence Number");
        ensureEnumConversion("APN_AND_RELATIVE_CAPACITY", "APN and Relative Capacity");
        ensureEnumConversion("WLAN_OFFLOADABILITY_INDICATION", "WLAN Offloadability Indication");
        ensureEnumConversion("PAGING_AND_SERVICE_INFORMATION", "Paging and Service Information");
        ensureEnumConversion("INTEGER_NUMBER", "Integer Number");
        ensureEnumConversion("MILLISECOND_TIME_STAMP", "Millisecond Time Stamp");
        ensureEnumConversion("MONITORING_EVENT_INFORMATION", "Monitoring Event Information");
        ensureEnumConversion("ECGI_LIST", "ECGI List");
        ensureEnumConversion("REMOTE_UE_CONTEXT", "Remote UE Context");
        ensureEnumConversion("REMOTE_USER_ID", "Remote User ID");
        ensureEnumConversion("REMOTE_UE_IP_INFORMATION", "Remote UE IP information");
        ensureEnumConversion("CIOT_OPTIMIZATIONS_SUPPORT_INDICATION", "CIoT Optimizations Support Indication");
        ensureEnumConversion("SCEF_PDN_CONNECTION", "SCEF PDN Connection");
        ensureEnumConversion("HEADER_COMPRESSION_CONFIGURATION", "Header Compression Configuration");
        ensureEnumConversion("EPCO", "Extended Protocol Configuration Options (ePCO)");
        ensureEnumConversion("SERVING_PLMN_RATE_CONTROL", "Serving PLMN Rate Control");
        ensureEnumConversion("COUNTER", "Counter");
        ensureEnumConversion("MAPPED_UE_USAGE_TYPE", "Mapped UE Usage Type");
        ensureEnumConversion("SECONDARY_RAT_USAGE_DATA_REPORT", "Secondary RAT Usage Data Report");
        ensureEnumConversion("UP_FUNCTION_SELECTION_INDICATION_FLAGS", "UP Function Selection Indication Flags");
        ensureEnumConversion("MAXIMUM_PACKET_LOSS_RATE", "Maximum Packet Loss Rate");
        ensureEnumConversion("APN_RATE_CONTROL_STATUS", "APN Rate Control Status");
        ensureEnumConversion("EXTENDED_TRACE_INFORMATION", "Extended Trace Information");
        ensureEnumConversion("MONITORING_EVENT_EXTENSION_INFORMATION", "Monitoring Event Extension Information");
        ensureEnumConversion("EXTENSION", "Special IE type for IE Type Extension");
        ensureEnumConversion("PRIVATE_EXTENSION", "Private Extension");
    }

    /**
     * These are all the generated GTPv1 Information Elements and they have been manually
     * checked to ensure the names are what we want. If you change the converter and break any of
     * these tests, that also means that the actual name of the enum will change when the new code is
     * generated, which would be a breaking change and as such, any code using it would have to be updated.
     */
    @Test
    public void testConvertToEnumGtpv1() {
        ensureEnumConversion("CAUSE", "Cause");

        // Note how the conversion favors "abbreviations" where, in this case, instead of naming the
        // enum "INTERNATIONAL_MOBILE_SUBSCRIBER_IDENTITY" it favors the abbreviation IMSI
        ensureEnumConversion("IMSI", "International Mobile Subscriber Identity (IMSI)");
        ensureEnumConversion("RAI", "Routeing Area Identity (RAI)");
        ensureEnumConversion("TLLI", "Temporary Logical Link Identity (TLLI)");
        ensureEnumConversion("P_TMSI", "Packet TMSI (P-TMSI)");
        ensureEnumConversion("REORDERING_REQUIRED", "Reordering Required");
        ensureEnumConversion("AUTHENTICATION_TRIPLET", "Authentication Triplet");
        ensureEnumConversion("MAP_CAUSE", "MAP Cause");
        ensureEnumConversion("P_TMSI_SIGNATURE", "P-TMSI Signature");
        ensureEnumConversion("MS_VALIDATED", "MS Validated");
        ensureEnumConversion("RECOVERY", "Recovery");
        ensureEnumConversion("SELECTION_MODE", "Selection Mode");
        ensureEnumConversion("TUNNEL_ENDPOINT_IDENTIFIER_DATA_I", "Tunnel Endpoint Identifier Data I");
        ensureEnumConversion("TUNNEL_ENDPOINT_IDENTIFIER_CONTROL_PLANE", "Tunnel Endpoint Identifier Control Plane");
        ensureEnumConversion("TUNNEL_ENDPOINT_IDENTIFIER_DATA_II", "Tunnel Endpoint Identifier Data II");
        ensureEnumConversion("TEARDOWN_IND", "Teardown Ind");
        ensureEnumConversion("NSAPI", "NSAPI");
        ensureEnumConversion("RANAP_CAUSE", "RANAP Cause");
        ensureEnumConversion("RAB_CONTEXT", "RAB Context");
        ensureEnumConversion("RADIO_PRIORITY_SMS", "Radio Priority SMS");
        ensureEnumConversion("RADIO_PRIORITY", "Radio Priority");
        ensureEnumConversion("PACKET_FLOW_ID", "Packet Flow Id");
        ensureEnumConversion("CHARGING_CHARACTERISTICS", "Charging Characteristics");
        ensureEnumConversion("TRACE_REFERENCE", "Trace reference");
        ensureEnumConversion("TRACE_TYPE", "Trace type");
        ensureEnumConversion("MS_NOT_REACHABLE_REASON", "MS Not Reachable Reason");
        ensureEnumConversion("CHARGING_ID", "Charging ID");
        ensureEnumConversion("END_USER_ADDRESS", "End User Address");
        ensureEnumConversion("MM_CONTEXT", "MM Context");
        ensureEnumConversion("PDP_CONTEXT", "PDP Context");
        ensureEnumConversion("ACCESS_POINT_NAME", "Access Point Name");
        ensureEnumConversion("PROTOCOL_CONFIGURATION_OPTIONS", "Protocol Configuration Options");
        ensureEnumConversion("GSN_ADDRESS", "GSN Address");
        ensureEnumConversion("MSISDN", "MS International PSTN/ISDN Number (MSISDN)");
        ensureEnumConversion("QUALITY_OF_SERVICE_PROFILE", "Quality of Service Profile");
        ensureEnumConversion("AUTHENTICATION_QUINTUPLET", "Authentication Quintuplet");
        ensureEnumConversion("TRAFFIC_FLOW_TEMPLATE", "Traffic Flow Template");
        ensureEnumConversion("TARGET_IDENTIFICATION", "Target Identification");
        ensureEnumConversion("UTRAN_TRANSPARENT_CONTAINER", "UTRAN Transparent Container");
        ensureEnumConversion("RAB_SETUP_INFORMATION", "RAB Setup Information");
        ensureEnumConversion("EXTENSION_HEADER_TYPE_LIST", "Extension Header type List");
        ensureEnumConversion("TRIGGER_ID", "Trigger Id");
        ensureEnumConversion("OMC_IDENTITY", "OMC Identity");
        ensureEnumConversion("RAN_TRANSPARENT_CONTAINER", "RAN Transparent Container");
        ensureEnumConversion("PDP_CONTEXT_PRIORITIZATION", "PDP Context Prioritization");
        ensureEnumConversion("ADDITIONAL_RAB_SETUP_INFORMATION", "Additional RAB Setup Information");
        ensureEnumConversion("SGSN_NUMBER", "SGSN Number");
        ensureEnumConversion("COMMON_FLAGS", "Common Flags");
        ensureEnumConversion("APN_RESTRICTION", "APN Restriction");
        ensureEnumConversion("RADIO_PRIORITY_LCS", "Radio Priority LCS");
        ensureEnumConversion("RAT", "RAT type");
        ensureEnumConversion("USER_LOCATION_INFORMATION", "User Location Information");
        ensureEnumConversion("MS_TIME_ZONE", "MS Time Zone");
        ensureEnumConversion("SV", "IMEI(SV)");
        ensureEnumConversion("CAMEL_CHARGING_INFORMATION_CONTAINER", "CAMEL Charging Information Container");
        ensureEnumConversion("MBMS_UE_CONTEXT", "MBMS UE Context");
        ensureEnumConversion("TMGI", "Temporary Mobile Group Identity (TMGI)");
        ensureEnumConversion("RIM_ROUTING_ADDRESS", "RIM Routing Address");
        ensureEnumConversion("MBMS_PROTOCOL_CONFIGURATION_OPTIONS", "MBMS Protocol Configuration Options");
        ensureEnumConversion("MBMS_SERVICE_AREA", "MBMS Service Area");
        ensureEnumConversion("SOURCE_RNC_PDCP_CONTEXT_INFO", "Source RNC PDCP context info");
        ensureEnumConversion("ADDITIONAL_TRACE_INFO", "Additional Trace Info");
        ensureEnumConversion("HOP_COUNTER", "Hop Counter");
        ensureEnumConversion("SELECTED_PLMN_ID", "Selected PLMN ID");
        ensureEnumConversion("MBMS_SESSION_IDENTIFIER", "MBMS Session Identifier");
        ensureEnumConversion("MBMS2G3G_INDICATOR", "MBMS 2G/3G Indicator");
        ensureEnumConversion("ENHANCED_NSAPI", "Enhanced NSAPI");
        ensureEnumConversion("MBMS_SESSION_DURATION", "MBMS Session Duration");
        ensureEnumConversion("ADDITIONAL_MBMS_TRACE_INFO", "Additional MBMS Trace Info");
        ensureEnumConversion("MBMS_SESSION_REPETITION_NUMBER", "MBMS Session Repetition Number");
        ensureEnumConversion("MBMS_TIME_TO_DATA_TRANSFER", "MBMS Time To Data Transfer");
        ensureEnumConversion("BSS_CONTAINER", "BSS Container");
        ensureEnumConversion("CELL_IDENTIFICATION", "Cell Identification");
        ensureEnumConversion("PDU_NUMBERS", "PDU Numbers");
        ensureEnumConversion("BSSGP_CAUSE", "BSSGP Cause");
        ensureEnumConversion("REQUIRED_MBMS_BEARER_CAPABILITIES", "Required MBMS bearer capabilities");
        ensureEnumConversion("RIM_ROUTING_ADDRESS_DISCRIMINATOR", "RIM Routing Address Discriminator");
        ensureEnumConversion("LIST_OF_SET_UP_PFCS", "List of set-up PFCs");
        ensureEnumConversion("PS_HANDOVER_XID_PARAMETERS", "PS Handover XID Parameters");
        ensureEnumConversion("MS_INFO_CHANGE_REPORTING_ACTION", "MS Info Change Reporting Action");
        ensureEnumConversion("DIRECT_TUNNEL_FLAGS", "Direct Tunnel Flags");
        ensureEnumConversion("CORRELATION_ID", "Correlation-ID");
        ensureEnumConversion("BEARER_CONTROL_MODE", "Bearer Control Mode");
        ensureEnumConversion("MBMS_FLOW_IDENTIFIER", "MBMS Flow Identifier");
        ensureEnumConversion("MBMS_IP_MULTICAST_DISTRIBUTION", "MBMS IP Multicast Distribution");
        ensureEnumConversion("MBMS_DISTRIBUTION_ACKNOWLEDGEMENT", "MBMS Distribution Acknowledgement");
        ensureEnumConversion("RELIABLE_INTER_RAT_HANDOVER_INFO", "Reliable INTER RAT HANDOVER INFO ");
        ensureEnumConversion("RFSP_INDEX", "RFSP Index");
        ensureEnumConversion("FQDN", "Fully Qualified Domain Name (FQDN)");
        ensureEnumConversion("EVOLVED_ALLOCATION_RETENTION_PRIORITY_I", "Evolved Allocation/Retention Priority I");
        ensureEnumConversion("EVOLVED_ALLOCATION_RETENTION_PRIORITY_II", "Evolved Allocation/Retention Priority II");
        ensureEnumConversion("EXTENDED_COMMON_FLAGS", "Extended Common Flags");
        ensureEnumConversion("UCI", "User CSG Information (UCI)");
        ensureEnumConversion("CSG_INFORMATION_REPORTING_ACTION", "CSG Information Reporting Action");
        ensureEnumConversion("CSG_ID", "CSG ID");
        ensureEnumConversion("CMI", "CSG Membership Indication (CMI)");
        ensureEnumConversion("AMBR", "Aggregate Maximum Bit Rate (AMBR)");
        ensureEnumConversion("UE_NETWORK_CAPABILITY", "UE Network Capability");
        ensureEnumConversion("UE_AMBR", "UE-AMBR");
        ensureEnumConversion("APN_AMBR_WITH_NSAPI", "APN-AMBR with NSAPI");
        ensureEnumConversion("GGSN_BACK_OFF_TIME", "GGSN Back-Off Time");
        ensureEnumConversion("SIGNALLING_PRIORITY_INDICATION", "Signalling Priority Indication");
        ensureEnumConversion("SIGNALLING_PRIORITY_INDICATION_WITH_NSAPI", "Signalling Priority Indication with NSAPI");
        ensureEnumConversion("HIGHER_BITRATES_THAN16MBPS_FLAG", "Higher bitrates than 16 Mbps flag");
        ensureEnumConversion("ADDITIONAL_MM_CONTEXT_FOR_SRVCC", "Additional MM context for SRVCC");
        ensureEnumConversion("ADDITIONAL_FLAGS_FOR_SRVCC", "Additional flags for SRVCC");
        ensureEnumConversion("STN_SR", "STN-SR");
        ensureEnumConversion("CMSISDN", "C-MSISDN");
        ensureEnumConversion("EXTENDED_RANAP_CAUSE", "Extended RANAP Cause");
        ensureEnumConversion("ENODEB_ID", "eNodeB ID");
        ensureEnumConversion("SELECTION_MODE_WITH_NSAPI", "Selection Mode with NSAPI");
        ensureEnumConversion("ULI_TIMESTAMP", "ULI Timestamp");
        ensureEnumConversion("LHN_ID", "Local Home Network ID (LHN-ID) with NSAPI");
        ensureEnumConversion("CN_OPERATOR_SELECTION_ENTITY", "CN Operator Selection Entity ");
        ensureEnumConversion("UE_USAGE_TYPE", "UE Usage type");
        ensureEnumConversion("EXTENDED_COMMON_FLAGS_II", "Extended Common Flags II");
        ensureEnumConversion("NODE_IDENTIFIER", "Node Identifier ");
        ensureEnumConversion("CIOT_OPTIMIZATIONS_SUPPORT_INDICATION", "CIoT Optimizations Support Indication");
        ensureEnumConversion("SCEF_PDN_CONNECTION", "SCEF PDN Connection");
        ensureEnumConversion("IOV_UPDATES_COUNTER", "IOV_updates counter");
        ensureEnumConversion("MAPPED_UE_USAGE_TYPE", "Mapped UE Usage type");
        ensureEnumConversion("UP_FUNCTION_SELECTION_INDICATION_FLAGS", "UP Function Selection Indication Flags");
        ensureEnumConversion("EXTENSION", "Special IE type for IE type Extension");
        ensureEnumConversion("CHARGING_GATEWAY_ADDRESS", "Charging Gateway Address");
        ensureEnumConversion("PRIVATE_EXTENSION", "Private Extension");

    }

    @Test
    public void testConvertToEnum() {
        ensureEnumConversion("CAUSE", "Cause");
        ensureEnumConversion("CAUSE", "cause");
        ensureEnumConversion("HELLO_WORLD", "Hello-World");
        ensureEnumConversion("HELLO_WORLD", "Hello World");
        ensureEnumConversion("HELLO_WORLD", "Hello_World");
        ensureEnumConversion("HELLO_WORLD", "Hello_world");
        ensureEnumConversion("HELLOWORLD", "Helloworld");

    }

    @Test
    public void testRegularClassName() {
        ensureConversion("Tgpp2QosInformation", "3GPP2-QoS-Information", null);
        ensureConversion("InternationalMobileSubscriberIdentity", "International Mobile Subscriber Identity (IMSI)", "Imsi");
        ensureConversion("ChargingGatewayAddress", "Charging Gateway Address", null);
        ensureConversion("OutgoingTrunkGroupId", "Outgoing-Trunk-Group-ID", null);

        // many spaces...
        ensureConversion("ChargingGatewayAddress", "    Charging     Gateway Address     ", null);
        // tabs...
        ensureConversion("ChargingGatewayAddress", "Charging    Gateway Address             ", null);

        // don't know why this would ever be specified this way but if it does, the current strategy is still
        // to detect and remove it from the "main" name and return it as an abbreviation.
        ensureConversion("AbbreviationInTheMiddle", "Abbreviation (MIDDLE) In The Middle", "Middle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoAbbreviations() {
        converter.convert("Two Abbreviations (ONE) (TWO)");
    }


    private void ensureEnumConversion(final String expected, final String name) {
        final var actual = converter.convertToEnum(name);
        assertThat(actual, is(expected));
    }

    private void ensureConversion(final String expected, final String name, final String abbreviation) {
        final var result = converter.convertPreserveAbbreviation(name);
        assertThat(result.get(0), is(expected));

        // also verify that the method that doesn't preserve the abbreviation does the right thing
        assertThat(converter.convert(name), is(expected));


        if (abbreviation == null) {
            assertThat(result.size(), is(1));
        } else {
            assertThat(result.get(1), is(abbreviation));
        }
    }


}