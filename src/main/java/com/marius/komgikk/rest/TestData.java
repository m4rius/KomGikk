package com.marius.komgikk.rest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.domain.TimeEvent;
import com.marius.komgikk.service.UserService;
import com.marius.komgikk.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/testdata")
public class TestData {

    @GET
    public String storeTestData() {

        //Get user. Check that it is admin user
        Preconditions.checkArgument(UserServiceFactory.getUserService().isUserAdmin());

        KomGikkUser user = new UserService().getCurrentUser();
        if (user.getName() == null) {
            user.setName("TestUser");
            user.store();
        }

        //DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
        cleanAllEntities();


        //Check that default activities is stored. If not; store
        List<Activity> activities = Activity.allCurrent(user);
        if (!hasDefaultActivities(activities)) {
            Activity.storeDefaults(user);
            activities = Activity.allCurrent(user);
        }

        Activity kom = findDefault(activities, Activity.DefaultActivities.START);
        Activity gikk = findDefault(activities, Activity.DefaultActivities.END);

        //Store test activities
        Activity hentePosten = new Activity(user, "Hente posten", "1111", null).store();
        Activity ryddeBadet = new Activity(user, "Rydde badet", "2222", null).store();
        Activity sovePaaSofa = new Activity(user, "Sove på sofaen", "3333", null).store();
        Activity lageMiddag = new Activity(user, "Lage middag", "4444", null).store();
        Activity sePaaTv = new Activity(user, "Se på TV", "4444", null).store();

        //Store events
        DateTime now = DateUtil.now();
        DateTime date = now.withDayOfWeek(DateTimeConstants.MONDAY);


        while (date.isBefore(now) || date.equals(now)) {
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 7, 34, date.getZone()), kom).store();
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 7, 40, date.getZone()), ryddeBadet).store();
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 9, 12, date.getZone()), sovePaaSofa).store();
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 12, 37, date.getZone()), ryddeBadet).store();
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 16, 2, date.getZone()), lageMiddag).store();
            new TimeEvent(user, new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 17, 7, date.getZone()), gikk).store();
            date = date.plusDays(1);
        }

        return "OK";
    }

    private void cleanAllEntities() {
        DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query(Activity.kind);
        Iterable<Entity> activities = dataStore.prepare(q).asIterable();
        for (Entity activity : activities) {
            dataStore.delete(activity.getKey());
        }

        Query q2 = new Query(TimeEvent.kind);
        Iterable<Entity> events = dataStore.prepare(q2).asIterable();
        for (Entity event : events) {
            dataStore.delete(event.getKey());
        }
    }

    private Activity findDefault(List<Activity> activities, Activity.DefaultActivities type) {
        for (Activity activity : activities) {
            if (activity.getDefaultType() == type) {
                return activity;
            }
        }

        throw new IllegalStateException("Fant ikke START");
    }

    private boolean hasDefaultActivities(List<Activity> activities) {
        int i = 0;
        for (Activity activity : activities) {
            if (activity.getDefaultType() != null)
                i++;
        }
        return i == 4;
    }
}
