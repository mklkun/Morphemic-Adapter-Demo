// Acquire variables
def instanceName = variables.get("INSTANCE_NAME")

// Handle service parameters
def ipAddr = new File(instanceName+"_dbipaddr").text.trim()
def port = new File(instanceName+"_dbport").text.trim()

// Push variables
variables.put("DB_IPADDR", ipAddr)
variables.put("DB_PORT", port)