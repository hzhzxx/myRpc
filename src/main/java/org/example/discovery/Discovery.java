package org.example.discovery;

import java.util.List;

public interface Discovery {

    List<String> getServiceAddress(String name);
}
