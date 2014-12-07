function LoadingState() {
    this.userLoaded = false;
    this.activitiesLoaded = false;
    this.timeEventLoaded = false;

    this.isReadyForUserInteraction = function() {
        console.log("isReady");
        return this.userLoaded && this.activitiesLoaded && this.timeEventLoaded;
    }
}