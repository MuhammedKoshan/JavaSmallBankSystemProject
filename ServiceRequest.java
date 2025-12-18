package BankSystem;
public class ServiceRequest implements Comparable<ServiceRequest> {

    private final String customerId;
    private final String issue;
    private final int priority; // 1 = HIGH, 2 = MED, 3 = LOW

    public ServiceRequest(String customerId, String issue, int priority) {
        this.customerId = customerId;
        this.issue = issue;
        this.priority = priority;
    }

    public String getCustomerId() { return customerId; }
    public String getIssue() { return issue; }
    public int getPriority() { return priority; }

    @Override
    public int compareTo(ServiceRequest other) {
        // smaller priority value => higher urgency, so return comparison ascending
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return "Req[cust=" + customerId + ", p=" + priority + ", issue=" + issue + "]";
    }
}
