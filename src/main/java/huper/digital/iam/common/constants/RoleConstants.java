package huper.digital.iam.common.constants;

/**
 * Constantes de permissões para @RolesAllowed. Descrições e lista dinâmica vêm de {@link huper.digital.iam.permission.service.PermissionService}.
 */
public final class RoleConstants {

  private RoleConstants() {}

  public static final String DASHBOARD_READ = "DASHBOARD_READ";
  public static final String CUSTOMER_CREATE = "CUSTOMER_CREATE";
  public static final String CUSTOMER_READ = "CUSTOMER_READ";
  public static final String CUSTOMER_WRITE = "CUSTOMER_WRITE";
  public static final String CUSTOMER_DELETE = "CUSTOMER_DELETE";
  public static final String APPOINTMENT_CREATE = "APPOINTMENT_CREATE";
  public static final String APPOINTMENT_READ = "APPOINTMENT_READ";
  public static final String APPOINTMENT_WRITE = "APPOINTMENT_WRITE";
  public static final String APPOINTMENT_DELETE = "APPOINTMENT_DELETE";
  public static final String APPOINTMENT_CANCEL = "APPOINTMENT_CANCEL";
  public static final String PROFESSIONAL_READ = "PROFESSIONAL_READ";
  public static final String PROFESSIONAL_WRITE = "PROFESSIONAL_WRITE";
  public static final String PROFESSIONAL_DELETE = "PROFESSIONAL_DELETE";
  public static final String SERVICE_READ = "SERVICE_READ";
  public static final String SERVICE_WRITE = "SERVICE_WRITE";
  public static final String SERVICE_DELETE = "SERVICE_DELETE";
  public static final String FINANCIAL_READ = "FINANCIAL_READ";
  public static final String FINANCIAL_WRITE = "FINANCIAL_WRITE";
  public static final String FINANCIAL_APPROVE = "FINANCIAL_APPROVE";
  public static final String PAYMENT_READ = "PAYMENT_READ";
  public static final String PAYMENT_WRITE = "PAYMENT_WRITE";
  public static final String PAYMENT_APPROVE = "PAYMENT_APPROVE";
  public static final String EXPENSE_READ = "EXPENSE_READ";
  public static final String EXPENSE_WRITE = "EXPENSE_WRITE";
  public static final String EXPENSE_APPROVE = "EXPENSE_APPROVE";
  public static final String PRODUCT_READ = "PRODUCT_READ";
  public static final String PRODUCT_WRITE = "PRODUCT_WRITE";
  public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
  public static final String REPORT_READ = "REPORT_READ";
  public static final String REPORT_GENERATE = "REPORT_GENERATE";
  public static final String REPORT_EXPORT = "REPORT_EXPORT";
  public static final String USER_MANAGEMENT_READ = "USER_MANAGEMENT_READ";
  public static final String USER_MANAGEMENT_WRITE = "USER_MANAGEMENT_WRITE";
  public static final String USER_MANAGEMENT_DELETE = "USER_MANAGEMENT_DELETE";
  public static final String GROUP_MANAGEMENT_READ = "GROUP_MANAGEMENT_READ";
  public static final String GROUP_MANAGEMENT_WRITE = "GROUP_MANAGEMENT_WRITE";
  public static final String GROUP_MANAGEMENT_DELETE = "GROUP_MANAGEMENT_DELETE";
}
