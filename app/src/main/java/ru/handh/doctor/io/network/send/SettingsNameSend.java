package ru.handh.doctor.io.network.send;

import ru.handh.doctor.io.network.responce.ModelDoctor;

/**
 * Created by sgirn on 11.11.2015.
 */
public class SettingsNameSend {
    private String token;
    private String name;
    private String lastName;
    private String middleName;
    private ModelDoctor.Address address;

    public SettingsNameSend(String token, String name, String lastName, String middleName, ModelDoctor.Address address) {
        this.token = token;
        this.name = name;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
    }
}
