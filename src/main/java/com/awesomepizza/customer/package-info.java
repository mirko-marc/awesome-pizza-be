@org.springframework.modulith.ApplicationModule(
        displayName = "Customer",
        allowedDependencies = {
                "shared :: dto",
                "shared :: entity",
                "shared :: enumeration",
                "shared :: exception",
                "shared :: mapper",
                "shared :: model",
                "shared :: repository"
        }
)
package com.awesomepizza.customer;
