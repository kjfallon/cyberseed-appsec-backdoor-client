### CyberSeed 2017
### Application Security Competition
### Secure Medical Information Repository Kit (SMIRK)

Server (https://github.com/kjfallon/cyberseed-appsec)  
Integration Test Client (https://github.com/kjfallon/cyberseed-appsec-integration-client)  
Backdoor Client (https://github.com/kjfallon/cyberseed-appsec-backdoor-client)

### Backdoor Client

The backdoor client will be used to load data directly into the SMIRK Server Applications data store. Teams will provide a Backdoor Client as a separate Docker image that provides functionality to update all of the data fields defined in the data dictionary. 

The backdoor client application SHALL be named [teamname]BackdoorClient.[jar|py]

The backdoor client must provide the following functions callable as command line parameters.

- [x] Author standalone direct DB client
- [x] setITAdmin
- [x] loadData
- [x] loadBackupCfg
- [x] getBackupCfg
- [x] DumpDB
