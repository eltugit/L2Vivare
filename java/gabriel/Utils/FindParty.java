package gabriel.Utils;

import gabriel.config.GabConfig;
import gabriel.listener.actor.player.OnQuestionMarkListener;
import gabriel.listener.model.CharListenerList;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.PartyDistributionType;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.AskJoinParty;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.SystemMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class FindParty implements IVoicedCommandHandler {
    private static final String[] VOICED_COMMANDS = {"party", "invite", "partylist"};
    private static final int PARTY_REQUEST_DURATION = 600_000; // 10 minutes delay until the request is rendered invalid.
    private static final int PARTY_REQUEST_DELAY = 60_000; // 1 minute delay until you can send a party request to the whole server again.
    private static final OnPartyQuestionMarkClicked LISTENER = new OnPartyQuestionMarkClicked();
    static final Map<Integer, FindPartyRequest> _requests = new HashMap<Integer, FindPartyRequest>(); // PartyRequestObjId, RequestorPlayerObjId
    @SuppressWarnings("unused")
    private static ScheduledFuture<?> _requestsCleanupTask = null;

    static {
        CharListenerList.addGlobal(LISTENER);
        _requestsCleanupTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate((new Runnable() {
            @Override
            public void run() {
                synchronized (_requests) // Dont touch me while cleaning-up!
                {
                    for (Map.Entry<Integer, FindPartyRequest> entry : _requests.entrySet()) {
                        if (entry.getValue().requestStartTimeMilis + PARTY_REQUEST_DURATION < System.currentTimeMillis())
                            _requests.remove(entry.getKey());
                    }
                }
            }
        }), 60000, 60000);
    }

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
        if (command.startsWith("partylist")) {
            activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.BATTLEFIELD, "[Party Request]", "---------=[List Party Requests]=---------"));

            for (FindPartyRequest request : _requests.values()) {
                // .partylist freya -> will result in searching party requests for freya only.
                if (target != null && !target.isEmpty() && !request.message.toLowerCase().contains(target.toLowerCase()))
                    continue;
                L2PcInstance partyLeader = L2World.getInstance().getPlayer(request.requestorObjId);
                if (partyLeader == null)
                    continue;
                int freeSlots = 9 - 1; // One taken by the party leader.
                if (partyLeader.getParty() != null)
                    freeSlots = 9 - partyLeader.getParty().getMemberCount();
                if (freeSlots <= 0)
                    continue;

                int partyRequestObjId = 0;
                for (Map.Entry<Integer, FindPartyRequest> entry : _requests.entrySet()) {
                    if (entry.getValue().requestorObjId == partyLeader.getObjectId()) {
                        partyRequestObjId = entry.getKey();
                        break;
                    }
                }
                activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.PARTY, "[Find Party]", "\b\tType=1 \tID=" + partyRequestObjId + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b" + partyLeader.getName() + " (" + freeSlots + "/" + L2Party.MAX_SIZE + ")" + " free slots. " + request.message));

            }
            activeChar.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.BATTLEFIELD, "[Party Request]", "---------=[End Party Requests]=---------"));
            return true;
        } else if (command.startsWith("invite")) {
            L2PcInstance playerToInvite = null;
            if (activeChar.getParty().getMemberCount() == L2Party.MAX_SIZE) {
                activeChar.sendMessage("Your party is full!");
                return false;
            }
            if (activeChar.isInParty() && !activeChar.getParty().isLeader(activeChar) && activeChar.getParty().getMemberCount() != 9)
                playerToInvite = (L2PcInstance) activeChar.getTarget();
            if (playerToInvite != null && activeChar != playerToInvite) // A party member asks the party leader to invite specified player.
            {
                Collection<L2PcInstance> pls = L2World.getInstance().getPlayers();
                for (L2PcInstance ptMem : pls) {
                    if (activeChar.getParty().getLeader() == ptMem)
                        //ptMem.sendPacket(new CreatureSay(ptMem.getObjectId(), Say2.PARTY, "[Party Request]", "Please invite " + playerToInvite.getName() + " to the party. \b\tType=1 \tID=" + playerToInvite.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));
                        ptMem.sendPacket(new CreatureSay(ptMem.getObjectId(), Say2.PARTY, "[" + ptMem.getName() + "]", "Please invite " + playerToInvite.getName() + " to the party. \b\tType=1 \tID=" + playerToInvite.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));

                    else
                        //ptMem.sendPacket(new CreatureSay(ptMem.getObjectId(), Say2.PARTY, "[Party Request]", "Please invite " + playerToInvite.getName() + " to the party. By: " +ptMem.getName()));
                        ptMem.sendPacket(new CreatureSay(ptMem.getObjectId(), Say2.PARTY, "[" + ptMem.getName() + "]", "Please invite " + playerToInvite.getName() + " to the party."));
                }
            }
            return true;
        } else if (command.startsWith("party")) // The party leader requests whole server for party members.
        {
            // Only party leaders can use it.
            if (activeChar.isInParty() && !activeChar.getParty().isLeader(activeChar)) {
                activeChar.sendMessage("Only your party leaader can use this command now.");
                return true;
            }
            // Only players 40+
            if (activeChar.getLevel() < 40) {
                activeChar.sendMessage("Command available only for players lvl 40+");
                return true;
            }
            int partyRequestObjId = 0;
            for (Map.Entry<Integer, FindPartyRequest> entry : _requests.entrySet()) {
                if (entry.getValue().requestorObjId == activeChar.getObjectId()) {
                    partyRequestObjId = entry.getKey();
                    break;
                }
            }
            if (partyRequestObjId == 0)
                partyRequestObjId = IdFactory.getInstance().getNextId();
            int freeSlots = 9 - 1; // One taken by the party leader.
            if (activeChar.getParty() != null)
                freeSlots = 9 - activeChar.getParty().getMemberCount();
            if (freeSlots <= 0) {
                activeChar.sendMessage("Your party is full. Try again when you have free slots.");
                return true;
            }
            if (target != null && !target.isEmpty()) {
                target = String.valueOf(target.charAt(0)).toUpperCase() + target.substring(1);
            }
            FindPartyRequest request = _requests.get(partyRequestObjId);
            if (request == null) {
                request = new FindPartyRequest(activeChar, target);
            } else {
                long delay = System.currentTimeMillis() - request.requestStartTimeMilis;
                if (delay < PARTY_REQUEST_DELAY) {
                    activeChar.sendMessage("You can send a request every " + PARTY_REQUEST_DELAY / 1000 + " seconds. " + (PARTY_REQUEST_DELAY - delay) / 1000 + " seconds remaining until you can try again.");
                    return true;
                }
                if (target == null || target.isEmpty())
                    request.update(); // Update perserving the message so players can type only .party, but displaying the same message as before.
                else
                    request.update(target); // Update with overriding the message
            }
            _requests.put(partyRequestObjId, request);
            //                                                                                                                             [Party Find]: [?] Nik (3/9) free slots. Message
            for (L2PcInstance player : L2World.getInstance().getPlayers()) {
                // Do not display to players who cant join party, but display to the requesting party so they can see their own message working.
                if (GabUtils.canJoinParty(activeChar, player) && player != activeChar) {
                    player.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.PARTY, "[Find Party]", "\b\tType=1 \tID=" + partyRequestObjId + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b" + activeChar.getName() + " (" + freeSlots + "/" + 9 + ")" + " free slots. " + request.message));
                    //player.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.PARTY, "[Find Party]", "\b\tType=1 \tID=" + activeChar.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b" + activeChar.getName() + " (" + freeSlots + "/" + L2Party.MAX_SIZE +")" + " free slots. " + request.message));
                }
                if (player == activeChar) {
                    player.sendPacket(new CreatureSay(activeChar.getObjectId(), Say2.PARTY, "[Find Party]", "Your Request has been posted. All players in the server that are valid to enter a party, will receive your Message. Keep an eye for possible recruits!"));
                }
            }
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return VOICED_COMMANDS;
    }

    private static class OnPartyQuestionMarkClicked implements OnQuestionMarkListener {
        @Override
        public void onQuestionMarkClicked(L2PcInstance player, int targetObjId) {
            int requestorObjId = _requests.containsKey(targetObjId) ? _requests.get(targetObjId).requestorObjId : 0;
            if (requestorObjId > 0) // Its a regular party request to the server for additional party members.
            {
                if (player.getObjectId() != requestorObjId) {
                    L2PcInstance partyLeader = L2World.getInstance().getPlayer(requestorObjId);
                    if (partyLeader == null) {
                        player.sendMessage("Party leader is offline.");
                    } else// if (partyLeader.isInParty())
                    {
                        //requestParty(partyLeader, player);
                        long delay = System.currentTimeMillis() - player.getQuickVarL("partyrequestsent", 0);
                        if (delay < PARTY_REQUEST_DELAY) {
                            player.sendMessage("You can send a request every " + PARTY_REQUEST_DELAY / 1000 + " seconds. " + (PARTY_REQUEST_DELAY - delay) / 1000 + " seconds remaining until you can try again.");
                            return;
                        }
                        if (player.getParty() != null) {
                            player.sendMessage("You are already in a party!");
                            return;
                        }
                        player.addQuickVar("partyrequestsent", System.currentTimeMillis());
                        partyLeader.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, "[Player Info]", "I'm, " + player.getName() + "  Level: " + player.getLevel() + ", Class: " + player.getClassId().name().toUpperCase() + ". Invite \b\tType=1 \tID=" + player.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));


//                        Inventory inv = player.getInventory();
//                        int playerWeaponObjectId = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND).getObjectId();
//                        L2Object obj = L2World.getInstance().findObject(playerWeaponObjectId);
//                        if (obj instanceof L2ItemInstance)
//                        {
//                            L2ItemInstance itm = (L2ItemInstance) obj;
//                            itm.publish();
//                            partyLeader.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, "[Player Info]", "And im using: "+itm.getItem().getName()+"  \b\tType=1 \tID=" + playerWeaponObjectId + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));
//                        }
//
                        player.sendMessage("Party request sent to " + partyLeader.getName());
                    }
                }
            } else {
                L2PcInstance target = L2World.getInstance().getPlayer(targetObjId); // Looks like a party request within a party to invite a certain member.
                if (target != null)
                    requestParty(player, target);
                    //requestParty(target, player);
                else
                    //player.sendMessage("The request is no longer valid");
                    player.sendMessage("");
            }
        }

        private void requestParty(L2PcInstance partyLeader, L2PcInstance target) {
            if (partyLeader.isOutOfControl()) {
                partyLeader.sendMessage("You are out of control!");
                return;
            }
            if (partyLeader.isProcessingRequest()) {
                partyLeader.sendMessage("waiting for another reply!");
                return;
            }
            if (target == null) {
                partyLeader.sendMessage("Player is offline");
                return;
            }
            if (target == partyLeader) {
                partyLeader.sendMessage("Incorrect target");
                return;
            }

            if (partyLeader.isInParty()) {
                if (partyLeader.getParty().getMemberCount() == L2Party.MAX_SIZE) {
                    partyLeader.sendMessage("Party is full");
                    return;
                }
                // Only the Party Leader may invite new members

//                if (Config.PARTY_LEADER_ONLY_CAN_INVITE && !partyLeader.getParty().isLeader(partyLeader))
//                {
//                    partyLeader.sendMessage("Only the leader can invite!");
//                    return;
//                }
                if (partyLeader.getParty().isInDimensionalRift()) {
                    partyLeader.sendMessage("You are in the Dimensional Rift");
                    return;
                }
            }
//            if (partyLeader.getParty().isInDimensionalRift())
//            {
//                partyLeader.sendMessage("You cannot invite a player when you are in the Dimensional Rift.");
//                return;
//            }

            if (!partyLeader.isInParty()) {
                createNewParty(target, partyLeader);
            } else {
                addTargetToParty(target, partyLeader);
            }
//            int itemDistribution = partyLeader.getParty() == null ? 0 : partyLeader.getParty().getLootDistribution();

//            L2Party party = partyLeader.getParty();
//            if (party == null)
//                partyLeader.setParty(party = new L2Party(partyLeader, itemDistribution));
            //target.joinParty(party);
            //partyLeader.sendPacket(JoinParty.SUCCESS);
        }
    }

    /**
     * @param target
     * @param requestor
     */
    static void addTargetToParty(L2PcInstance target, L2PcInstance requestor) {
        final L2Party party = requestor.getParty();
        SystemMessage msg;
        // summary of ppl already in party and ppl that get invitation
        if ((GabUtils.isInPvPInstance(requestor) && GabUtils.isInPvPInstance(target))) {

        } else {
            if (!party.isLeader(requestor)) {
                requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_LEADER_CAN_INVITE));
                return;
            }
        }
        if (target.isInParty()) {
            msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_IN_PARTY);
            msg.addString(target.getName());
            requestor.sendPacket(msg);
            return;
        }

        if (GabConfig.ALLOW_PARTY_LIMITATIONS) {
            if (!GabUtils.canInvite(requestor, target))
                return;
        }
        if (party.getMemberCount() >= 9) {
            requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PARTY_FULL));
            return;
        }
        if (party.getPendingInvitation() && !party.isInvitationRequestExpired()) {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        if (!target.isProcessingRequest()) {
            requestor.onTransactionRequest(target);
            // in case a leader change has happened, use party's mode
            target.sendPacket(new AskJoinParty(requestor.getName(), party.getDistributionType()));
            party.setPendingInvitation(true);

            if (Config.DEBUG) {
                _log.info("sent out a party invitation to:" + target.getName());
            }

        } else {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_BUSY_TRY_LATER);
            sm.addString(target.getName());
            requestor.sendPacket(sm);

            if (Config.DEBUG) {
                _log.warn(requestor.getName() + " already received a party invitation");
            }
        }
    }

    /**
     * @param target
     * @param requestor
     */
    static void createNewParty(L2PcInstance target, L2PcInstance requestor) {
        if (!target.isProcessingRequest()) {
            PartyDistributionType itemDistribution = requestor.getParty().getDistributionType();
            requestor.setParty(new L2Party(requestor, itemDistribution));

            requestor.onTransactionRequest(target);
            target.sendPacket(new AskJoinParty(requestor.getName(), itemDistribution));
            requestor.getParty().setPendingInvitation(true);

            if (Config.DEBUG) {
                _log.info("sent out a party invitation to:" + target.getName());
            }

        } else {
            requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);

            if (Config.DEBUG) {
                _log.warn(requestor.getName() + " already received a party invitation");
            }
        }
    }

    private static class FindPartyRequest {
        final int requestorObjId;
        long requestStartTimeMilis;
        String message;

        @SuppressWarnings("unused")
        public FindPartyRequest(L2PcInstance player) {
            requestorObjId = player.getObjectId();
            requestStartTimeMilis = System.currentTimeMillis();
            message = "";
        }

        public FindPartyRequest(L2PcInstance player, String msg) {
            requestorObjId = player.getObjectId();
            requestStartTimeMilis = System.currentTimeMillis();
            message = msg == null ? "" : msg;
        }

        public void update() {
            requestStartTimeMilis = System.currentTimeMillis();
        }

        public void update(String newMsg) {
            requestStartTimeMilis = System.currentTimeMillis();
            message = newMsg;
        }
    }
}

