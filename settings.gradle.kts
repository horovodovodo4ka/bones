if (System.getenv("JITPACK") != null) include (":bones")
else include (":bones", ":bones-sample")