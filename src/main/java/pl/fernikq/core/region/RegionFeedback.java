package pl.fernikq.core.region;

import pl.fernikq.core.config.MessagesManager;

public enum RegionFeedback {

    ALLOW(true),
    DENY(false),

    DENY_DESTROY_SPAWN(false,  MessagesManager.error("Nie mozesz niszczyc blokow na tym terenie!")),
    DENY_BUILD_SPAWN(false,  MessagesManager.error("Nie mozesz budowac na tym terenie!")),
    DENY_BUILD_PVP_Y(false,  MessagesManager.error("Podczas walki nie mozesz budowac ponizej kratki &c{Y}!")),
    DENY_DESTROY_GOLD_PICKAXE(false,  MessagesManager.error("Nie mozesz tutaj niszczyc blokow &eZlotym kilofem&8!")),
    DENY_BUCKETS(false,  MessagesManager.error("Nie mozesz tutaj uzywac wiaderek!")),
    DENY_PEARLS(false,  MessagesManager.error("Perly zostaly wylaczone na tym terenie!")),
    DENY_SPAWN_VEHICLES(false,  MessagesManager.error("Nie mozesz tutaj spawnowac pojazdow!")),
    DENY_PVP_OTHER_REGION(false,  MessagesManager.error("Nie mozesz atakowac graczy, znajdujac sie na na tym terenie!")),
    DENY_PROCCESS_COMMAND(false,  MessagesManager.error("Podana komenda zostala zablokowana!")),
    DENY_PROCCESS_COMMAND_FIGHT(false,  MessagesManager.error("Podana komenda nie jest dostepna podczas walki!")),
    DENY_PROCCESS_COMMAND_REGION(false,  MessagesManager.error("Podana komenda zostala zablokowana na tym terenie!")),
    DENY_PROCCESS_COMMAND_GUILD(false,  MessagesManager.error("Nie mozesz uzyc tej komendy na terenie wrogiej gildii!")),
    DENY_BUILD_GUILD(false,  MessagesManager.error("Nie mozesz budowac na terenie wrogiej gildii")),
    DENY_BUILD_GUILD_CAUSE_EXPLOSION(false,  MessagesManager.error("Budowanie zostalo zablokowane przez wybuch, blokada zakonczy sie za &c{TIME}")),
    DENY_BUILD_GUILD_PERMISSION(false,  MessagesManager.error("Nie posiadasz uprawnienia do budowania na terenie swojej gildii!")),
    DENY_BUILD_GUILD_CENTER(false,  MessagesManager.error("Nie mozesz budowac w centrum gildii!")),
    DENY_DESTROY_GUILD(false,  MessagesManager.error("Nie mozesz niszczyc blokow na terenie wrogiej gildii!")),
    DENY_DESTROY_GUILD_PERMISSION(false,  MessagesManager.error("Nie posiadasz uprawnienia do niszczenia blokow na terenie swojej gildii!")),
    DENY_DESTROY_GUILD_CENTER(false,  MessagesManager.error("Nie mozesz niszczyc blokow w centrum gildii")),
    DENY_BUCKETS_GUILD(false,  MessagesManager.error("Nie mozesz uzywac wiaderek na terenie wrogiej gildii!")),
    DENY_BUCKETS_GUILD_CENTER(false,  MessagesManager.error("Nie mozesz uzywac wiaderek w centrum gildii!")),
    DENY_SPAWN_VEHICLES_GUILD(false,  MessagesManager.error("Nie mozesz spawnowac pojazdow na terenie wrogiej gildii!")),
    DENY_SPAWN_VEHICLES_GUILD_CENTER(false,  MessagesManager.error("Nie mozesz spawnowac pojazdow w centrum gildii!")),
    DENY_PVP_OWN_GUILD(false,  MessagesManager.error("PVP w gildii jest wylaczone!")),
    DENY_PVP_ALLIANCE(false,  MessagesManager.error("Nie mozesz zaatakowac swojego sojusznika!")),
    DENY_ERROR(false,  MessagesManager.error("Wystapil blad, zglos sie do administracji!"));

    private String feedbackMessage;
    private final boolean permit;

    private RegionFeedback(boolean permit){
        this.permit = permit;
        this.feedbackMessage = "";
    }

    private RegionFeedback(boolean permit, String feedbackMessage){
        this.permit = permit;
        this.feedbackMessage = feedbackMessage;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public boolean isPermit() {
        return permit;
    }
}
