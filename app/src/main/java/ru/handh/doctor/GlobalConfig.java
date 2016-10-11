package ru.handh.doctor;

import ru.handh.doctor.io.network.responce.ModelConfig;

/**
 * Created by samsonov on 05.07.2016.
 */
public class GlobalConfig {

    //private static int docTimeworkId;

    public static void onConfigLoaded(ModelConfig modelConfig) {
        try {
            FluffyGeoService.LOCATION_UPDATE_INTERVAL = modelConfig.data.docUpdatingGeolocation*1000;
        } catch (Exception e) {
            //better safe than sorry
            e.printStackTrace();
        }

        //docTimeworkId = modelConfig.data.DOC_TIMEWORK_ID;
    }

//    public static boolean isHourly(int typeOfEmployment) {
//        return docTimeworkId==typeOfEmployment;
//    }
}
