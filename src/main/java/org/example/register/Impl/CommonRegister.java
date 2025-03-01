package org.example.register.Impl;


import org.example.register.Register;

import java.util.HashMap;
import java.util.Map;

public class CommonRegister implements Register {
    protected Map<String,Object> localRegister=new HashMap<>();


    @Override
    public void register(String serviceName, Object service) {
        localRegister.put(serviceName,service);
    }

    @Override
    public Object getService(String serviceName) {
        return localRegister.get(serviceName);
    }


}
