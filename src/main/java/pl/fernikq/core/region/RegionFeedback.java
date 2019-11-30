package pl.fernikq.core.region;

public enum RegionFeedback {

    ALLOW(true),
    DENY(false),
    DENY_DESTROY_SPAWN(false, "&8>> &fNie mozesz niszczyc na tym terenie&8!"),
    DENY_BUILD_SPAWN(false, "&8>> &fNie mozesz budowac na tym terenie&8!"),
    DENY_DESTROY_GOLD_PICKAXE(false, "&8>> &fNie mozesz tutaj niszczyc &eZlotym kilofem&8!"),
    DENY_BUCKETS(false, "&8>> &fNie mozesz tutaj uzywac wiaderek!"),
    DENY_PEARLS(false, "&8>> &fNie mozesz zostac przeteleportowany na ten region za pomoca perelki!"),
    DENY_SPAWN_VEHICLES(false, "&8>> &fNie mozesz tutaj spawnowac pojazdow!"),
    DENY_PVP_OTHER_REGION(false, "&8>> &fNie mozesz atakowac graczy ze spawnu!"),
    DENY_PROCCESS_COMMAND(false, "&8>> &fPodana komenda zostala zablokowana!"),
    DENY_PROCCESS_COMMAND_REGION(false, "&8>> &fPodana komenda zostala zablokowana na tym terenie!"),
    DENY_PROCCESS_COMMAND_GUILD(false, "&8>> &fNie mozesz uzyc podanej komendy na terenie wrogiej gildii!"),
    DENY_BUILD_GUILD(false, "&8>> &fNie mozesz budowac na terenie wrogiej gildii!"),
    DENY_BUILD_GUILD_PERMISSION(false, "&8>> &fNie posiadasz uprawnienia do budowania na terenie swojej gildii!"),
    DENY_DESTROY_GUILD(false, "&8>> &fNie mozesz niszczyc na terenie wrogiej gildii!"),
    DENY_DESTROY_GUILD_PERMISSION(false, "&8>> &fNie posiadasz uprawnienia do niszczenia na terenie swojej gildii!"),
    DENY_BUCKETS_GUILD(false, "&8>> &fNie mozesz uzywac wiaderek na terenie wrogiej gildii!"),
    DENY_SPAWN_VEHICLES_GUILD(false, "&8>> &fNie mozesz spawnowac pojazdow na terenie wrogiej gildii!"),
    DENY_PVP_OWN_GUILD(false, "&8>> &fPVP w gildii jest wylaczone!"),
    DENY_PVP_ALLIANCE(false, "&8>> &fNie mozesz zaatakowac swojego sojusznika!"),
    DENY_ERROR(false, "&8>> &fWystapil blad, zglos sie do administracji&8!");

    private String feedbackMessage;
    private final boolean permit;

    private RegionFeedback(boolean permit){
        this.permit = permit;
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
