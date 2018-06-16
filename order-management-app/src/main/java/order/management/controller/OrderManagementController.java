package order.management.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import order.management.order.ProductsOrder;
import order.management.order.ProductsOrderRepository;
import order.management.product.ProductRepository;
import order.management.proposal.Proposal;
import order.management.proposal.ProposalRepository;

@RestController()
public class OrderManagementController {

	private static final String PROPOSAL_PATH = "/proposal";
	private static final String ORDER_PATH = "/order";
	private static final String MANAGEMENT_BASE_PATH = "http://localhost:8090/shopkeeper/v1";

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProposalRepository proposalRepository;

	@Autowired
	ProductsOrderRepository orderRepository;

	@RequestMapping(value = "/orchestration/orders", method = RequestMethod.GET)
	public ResponseEntity<Iterable<ProductsOrder>> productsList(){
		return ResponseEntity.ok(orderRepository.findAll());
	}

	@RequestMapping(value = "/orchestration/order/{id}", method = RequestMethod.GET)
	public ResponseEntity<ProductsOrder> order(@PathVariable Long id, Model model){
		model.addAttribute("product", productRepository.findOne(id));
		return ResponseEntity.ok(orderRepository.findOne(id));
	}
	
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ProductsOrder> saveOrder(@RequestBody ProductsOrder order) {
		if (order.getStatus() != null && order.getDeliveryDate() != null && order.getPrice() != null){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		productRepository.save(order.getProducts());
		return ResponseEntity.status(HttpStatus.CREATED).body(orderRepository.save(order));
	}

	@RequestMapping(value = "/orchestration/order/{id}/manufactoring", method = RequestMethod.POST)
	public ResponseEntity<ProductsOrder> transitionOrdeManufactoring(@PathVariable Long id){
		ProductsOrder order = orderRepository.findOne(id);
		if (order.getStatus().equals(ProductsOrder.Status.Ordered)){
			order.setStatus(ProductsOrder.Status.Manufactoring);
			order = orderRepository.save(order);

			String uri = MANAGEMENT_BASE_PATH + ORDER_PATH + "/" + id;
			RestTemplate restTemplate = new RestTemplate();
			try{
				ProductsOrder orderToUpdade = new ProductsOrder();
				orderToUpdade.setDeliveryDate(order.getDeliveryDate());
				orderToUpdade.setPrice(order.getPrice());
				orderToUpdade.setStatus(order.getStatus());

				restTemplate.put(new URI(uri), orderToUpdade);
				return ResponseEntity.status(HttpStatus.CREATED).body(order);
			}
			catch(Exception e){
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@RequestMapping(value = "/orchestration/order/{id}/dispatch", method = RequestMethod.POST)
	public ResponseEntity<ProductsOrder> transitionOrdeDispatched(@PathVariable Long id){
		ProductsOrder order = orderRepository.findOne(id);
		if (order.getStatus().equals(ProductsOrder.Status.Manufactoring)){
			order.setStatus(ProductsOrder.Status.Dispatched);
			order= orderRepository.save(order);

			String uri = MANAGEMENT_BASE_PATH + ORDER_PATH + "/" + id;
			RestTemplate restTemplate = new RestTemplate();
			try{
				ProductsOrder orderToUpdade = new ProductsOrder();
				orderToUpdade.setDeliveryDate(order.getDeliveryDate());
				orderToUpdade.setPrice(order.getPrice());
				orderToUpdade.setStatus(order.getStatus());

				restTemplate.put(new URI(uri), orderToUpdade);
				return ResponseEntity.status(HttpStatus.CREATED).body(order);
			}
			catch(Exception e){
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@RequestMapping(value = "/orchestration/order/{id}/close", method = RequestMethod.POST)
	public ResponseEntity<ProductsOrder> transitionOrdeClosed(@PathVariable Long id){
		ProductsOrder order = orderRepository.findOne(id);
		if (order.getStatus().equals(ProductsOrder.Status.Dispatched)){
			order.setStatus(ProductsOrder.Status.Closed);
			orderRepository.save(order);

			String uri = MANAGEMENT_BASE_PATH + ORDER_PATH + "/" + id;
			RestTemplate restTemplate = new RestTemplate();
			try{
				ProductsOrder orderToUpdade = new ProductsOrder();
				orderToUpdade.setDeliveryDate(order.getDeliveryDate());
				orderToUpdade.setPrice(order.getPrice());
				orderToUpdade.setStatus(order.getStatus());

				restTemplate.put(new URI(uri), orderToUpdade);
				return ResponseEntity.status(HttpStatus.CREATED).body(order);
			}
			catch(Exception e){
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
	}

	@RequestMapping(value = "/orchestration/proposal/{id}",method = RequestMethod.GET)
	public ResponseEntity<Proposal> proposal(@PathVariable Long id){
		return ResponseEntity.ok(proposalRepository.findOne(id));
	}

	@RequestMapping(value = "/orchestration/proposal", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Proposal> saveProposal(@RequestBody Proposal proposal) {
		String orderId = proposal.getOrderRef() != null ? proposal.getOrderRef().toString() : null;
		if (proposal.getStatus() == null && orderId != null && orderRepository.findOne(Long.valueOf(orderId.substring(orderId.lastIndexOf("/")+1))) != null){
			proposal.setStatus(Proposal.Status.Open);
			proposal = proposalRepository.save(proposal);

			String uri = MANAGEMENT_BASE_PATH + PROPOSAL_PATH;
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Proposal> shopkeeperResponse = restTemplate.postForEntity(uri, proposal, Proposal.class);
			if (shopkeeperResponse.getStatusCode().equals(HttpStatus.CREATED)){
				return ResponseEntity.status(HttpStatus.CREATED).body(proposal);
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}

}