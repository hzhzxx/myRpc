package org.example.register;

public interface Register {
    void register(String serviceName,Object service);
    Object getService(String serviceName);
}
