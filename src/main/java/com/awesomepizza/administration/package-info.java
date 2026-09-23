@org.springframework.modulith.ApplicationModule(
        displayName = "Administration",
        allowedDependencies = {
                "shared :: entity",
                "shared :: enumeration",
                "shared :: exception",
                "shared :: mapper",
                "shared :: model",
                "shared :: repository"
        }
)
package com.awesomepizza.administration;
