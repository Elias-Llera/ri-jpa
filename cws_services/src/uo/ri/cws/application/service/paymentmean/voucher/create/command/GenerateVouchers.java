package uo.ri.cws.application.service.paymentmean.voucher.create.command;

import java.util.List;
import java.util.UUID;

import uo.ri.conf.Factory;
import uo.ri.cws.application.repository.ClientRepository;
import uo.ri.cws.application.repository.PaymentMeanRepository;
import uo.ri.cws.application.service.BusinessException;
import uo.ri.cws.application.util.command.Command;
import uo.ri.cws.domain.Client;
import uo.ri.cws.domain.Recommendation;
import uo.ri.cws.domain.Voucher;
import uo.ri.cws.domain.WorkOrder;

public class GenerateVouchers implements Command<Integer> {

	private PaymentMeanRepository pmRepo = Factory.repository.forPaymentMean();
	private ClientRepository clientRepo = Factory.repository.forClient();

	@Override
	public Integer execute() throws BusinessException {
		int generatedVouchers = 0;
		
		generatedVouchers += generateVouchersByRecommendation();
		generatedVouchers += generateVouchersByWorkOrders();
		generatedVouchers += generateVouchersByInvoice();
		
		return generatedVouchers;
	}
	
	private int generateVouchersByRecommendation() {
		List<Client> clients = clientRepo.findAll();
		int generated = 0;
		
		for (Client client : clients) {
			if(client.eligibleForRecommendationVoucher()) {
				List<Recommendation> validRecommendations = client.getRecommendationsValidForVoucher();
				Recommendation first = null, second = null;
				for (Recommendation recommendation : validRecommendations) {
					if (first == null) {
						first = recommendation;
					} else if (second == null) {
						second = recommendation;
					} else {
						first.markAsUsedForVoucher();
						second.markAsUsedForVoucher();
						recommendation.markAsUsedForVoucher();

						String code = UUID.randomUUID().toString();
						Voucher v = new Voucher(code, "By recommendation", 25,
								client);
						pmRepo.add(v);

						first = null;
						second = null;
						generated++;
					}
				}
			}
		}
		return generated;
	}
	
	private int generateVouchersByWorkOrders() {
		List<Client> clients = clientRepo.findAll();
		int generated = 0;

		for (Client client : clients) {
			List<WorkOrder> workOrders = client
					.getWorkOrdersAvailableForVoucher();

			WorkOrder first = null, second = null;

			for (WorkOrder wo : workOrders) {
				if (first == null) {
					first = wo;
				} else if (second == null) {
					second = wo;
				} else {
					first.markAsUsedForVoucher();
					second.markAsUsedForVoucher();
					wo.markAsUsedForVoucher();

					String code = UUID.randomUUID().toString();
					Voucher v = new Voucher(code, "By three workorders", 20,
							client);
					pmRepo.add(v);

					first = null;
					second = null;
					generated++;
				}
			}
		}
		return generated;
	}
	
	private int generateVouchersByInvoice() {
		List<Client> clients = clientRepo.findAll();
		int generated = 0;

		for (Client client : clients) {
			List<WorkOrder> workOrders = client
					.getWorkOrdersAvailableForVoucher();

			for (WorkOrder wo : workOrders) {
				if(wo.isInvoicedAndPaid() && wo.getInvoice().canGenerate500Voucher()) {
					wo.getInvoice().markAsUsed();
					String code = UUID.randomUUID().toString();
					Voucher v = new Voucher(code, "By invoice over 500", 30,
							client);
					pmRepo.add(v);
					generated++;
				}
			}
		}
		return generated;
	}
	

}
