if (System.getenv("JITPACK") != null || System.getenv("CI") != null) include (":bones")
else include (":bones", ":bones-sample")