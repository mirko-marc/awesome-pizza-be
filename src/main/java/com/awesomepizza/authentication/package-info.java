@org.springframework.modulith.ApplicationModule(
        displayName = "Authentication",
        allowedDependencies = {
                "shared :: dto",
                "shared :: entity",
                "shared :: exception"
        }
)
package com.awesomepizza.authentication;
