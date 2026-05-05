package com.iae.execution;

public class ProcessResult {

    private final int exitCode;
    private final String stdout;
    private final String stderr;
    private final boolean timedOut;

    public ProcessResult(int exitCode, String stdout, String stderr, boolean timedOut) {
        this.exitCode = exitCode;
        this.stdout   = stdout  != null ? stdout  : "";
        this.stderr   = stderr  != null ? stderr  : "";
        this.timedOut = timedOut;
    }

    public int getExitCode()    { return exitCode; }
    public String getStdout()   { return stdout;   }
    public String getStderr()   { return stderr;   }
    public boolean isTimedOut() { return timedOut; }

    public boolean hasFailed() {
        return timedOut || exitCode != 0;
    }

    public String getCombinedOutput() {
        if (stdout.isEmpty()) return stderr;
        if (stderr.isEmpty()) return stdout;
        return stdout + "\n" + stderr;
    }

    public boolean isSuccessful() {
        return !timedOut && exitCode == 0;
    }
}
