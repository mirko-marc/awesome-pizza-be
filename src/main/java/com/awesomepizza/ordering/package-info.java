@org.springframework.modulith.ApplicationModule(
        displayName = "Ordering",
        allowedDependencies = {
                "shared :: dto",
                "shared :: entity",
                "shared :: exception"
        }
)
package com.awesomepizza.ordering;
