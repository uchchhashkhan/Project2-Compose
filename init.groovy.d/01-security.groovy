import jenkins.model.*
import hudson.security.*

def instance = Jenkins.get()

// Create users (admin for setup, and a limited pipeline user)
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
if (hudsonRealm.getUser("admin") == null) {
  hudsonRealm.createAccount("admin", "ChangeMe_Admin123!")   // change after first login
}
if (hudsonRealm.getUser("pipeline") == null) {
  hudsonRealm.createAccount("pipeline", "ChangeMe_Pipeline123!") // used only for pipeline auth
}
instance.setSecurityRealm(hudsonRealm)

// Matrix-based authorization: disable anonymous, grant minimal to pipeline, admin full
def strategy = new GlobalMatrixAuthorizationStrategy()

// Admin: everything
strategy.add(Jenkins.ADMINISTER, "admin")

// Pipeline user: minimal safe set
[
  Jenkins.READ,
  Item.READ,
  Item.BUILD,
  Item.DISCOVER,
  Item.WORKSPACE,
  CredentialsProvider.USE_ITEM
].each { p -> strategy.add(p, "pipeline") }

instance.setAuthorizationStrategy(strategy)
instance.save()
