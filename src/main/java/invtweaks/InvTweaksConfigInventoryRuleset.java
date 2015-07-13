package invtweaks;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a whole configuration defined by rules. Several of them can be stored in the global configuration, as the mod
 * supports several rule configurations.
 *
 * @author Jimeo Wan
 */
public class InvTweaksConfigInventoryRuleset {
    private String name;
    private int[] lockPriorities;
    private boolean[] frozenSlots;
    private List<InvTweaksConfigSortingRule> rules;
    private List<String> autoReplaceRules;

    private InvTweaksItemTree tree;

    /**
     * Creates a new configuration holder. The configuration is not yet loaded.
     */
    public InvTweaksConfigInventoryRuleset(InvTweaksItemTree tree_, String name_) {
        tree = tree_;
        name = name_.trim();

        lockPriorities = new int[InvTweaksConst.INVENTORY_SIZE];
        for(int i = 0; i < lockPriorities.length; i++) {
            lockPriorities[i] = 0;
        }
        frozenSlots = new boolean[InvTweaksConst.INVENTORY_SIZE];
        for(int i = 0; i < frozenSlots.length; i++) {
            frozenSlots[i] = false;
        }

        rules = new ArrayList<>();
        autoReplaceRules = new ArrayList<>();
    }

    /**
     * @return If not null, returns the invalid keyword found
     * @throws InvalidParameterException
     */
    public String registerLine(String rawLine) throws InvalidParameterException {

        InvTweaksConfigSortingRule newRule;
        String lineText = rawLine.replaceAll("[\\s]+", " ").toLowerCase();
        String[] words = lineText.split(" ");

        // Parse valid lines only
        if(words.length == 2) {

            // Standard rules format
            if(lineText.matches("^([a-d]|[1-9]|[r]){1,2} [\\w]*$") || lineText
                    .matches("^[a-d][1-9]-[a-d][1-9][rv]?[rv]? [\\w]*$")) {

                words[0] = words[0].toLowerCase();
                words[1] = words[1];

                // Locking rule
                switch(words[1]) {
                    case InvTweaksConfig.LOCKED: {
                        int[] newLockedSlots = InvTweaksConfigSortingRule
                                .getRulePreferredPositions(words[0], InvTweaksConst.INVENTORY_SIZE,
                                        InvTweaksConst.INVENTORY_ROW_SIZE);
                        int lockPriority = InvTweaksConfigSortingRule.
                                getRuleType(words[0],
                                        InvTweaksConst.INVENTORY_ROW_SIZE)
                                .getLowestPriority() - 1;
                        for(int i : newLockedSlots) {
                            lockPriorities[i] = lockPriority;
                        }
                        return null;
                    }

                    // Freeze rule
                    case InvTweaksConfig.FROZEN: {
                        int[] newLockedSlots = InvTweaksConfigSortingRule
                                .getRulePreferredPositions(words[0], InvTweaksConst.INVENTORY_SIZE,
                                        InvTweaksConst.INVENTORY_ROW_SIZE);
                        for(int i : newLockedSlots) {
                            frozenSlots[i] = true;
                        }
                        return null;
                    }

                    // Standard rule
                    default:
                        String keyword = words[1].toLowerCase();
                        boolean isValidKeyword = tree.isKeywordValid(keyword);

                        // If invalid keyword, guess something similar,
                        // but check first if it's not an item ID
                        // (can be used to make rules for unknown items)
                        if(!isValidKeyword) {
                            if(keyword.matches("^[0-9-]*$")) {
                                isValidKeyword = true;
                            } else {
                                List<String> wordVariants = getKeywordVariants(keyword);
                                for(String wordVariant : wordVariants) {
                                    if(tree.isKeywordValid(wordVariant.toLowerCase())) {
                                        isValidKeyword = true;
                                        keyword = wordVariant;
                                        break;
                                    }
                                }
                            }
                        }

                        if(isValidKeyword) {
                            newRule = new InvTweaksConfigSortingRule(tree, words[0], keyword.toLowerCase(),
                                    InvTweaksConst.INVENTORY_SIZE,
                                    InvTweaksConst.INVENTORY_ROW_SIZE);
                            rules.add(newRule);
                            return null;
                        } else {
                            return keyword.toLowerCase();
                        }
                }
            }

            // Autoreplace rule
            else if(words[0].equals(InvTweaksConfig.AUTOREFILL) || words[0].equals("autoreplace")) { // Compatibility
                words[1] = words[1].toLowerCase();
                if(tree.isKeywordValid(words[1]) || words[1].equals(InvTweaksConfig.AUTOREFILL_NOTHING)) {
                    autoReplaceRules.add(words[1]);
                }
                return null;
            }
        }

        throw new InvalidParameterException();

    }

    public void finalizeRules() {

        // Default Autoreplace behavior
        if(autoReplaceRules.isEmpty()) {
            try {
                autoReplaceRules.add(tree.getRootCategory().getName());
            } catch(NullPointerException e) {
                throw new NullPointerException("No root category is defined.");
            }
        }

        // Sort rules by priority, highest first
        Collections.sort(rules, Collections.reverseOrder());
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the lockPriorities
     */
    public int[] getLockPriorities() {
        return lockPriorities;
    }

    /**
     * @return the frozenSlots
     */
    public boolean[] getFrozenSlots() {
        return frozenSlots;
    }

    /**
     * @return the rules
     */
    public List<InvTweaksConfigSortingRule> getRules() {
        return rules;
    }

    /**
     * @return the autoReplaceRules
     */
    public List<String> getAutoReplaceRules() {
        return autoReplaceRules;
    }

    /**
     * Compute keyword variants to also match bad keywords. torches => torch diamondSword => sworddiamond woodenPlank =>
     * woodPlank plankwooden plankwood
     */
    private static List<String> getKeywordVariants(String keyword) {
        List<String> variants = new ArrayList<>();

        if(keyword.endsWith("es")) { // ex: torches => torch
            variants.add(keyword.substring(0, keyword.length() - 2));
        }
        if(keyword.endsWith("s")) { // ex: wools => wool
            variants.add(keyword.substring(0, keyword.length() - 1));
        }

        if(keyword.contains("en")) { // ex: wooden => wood
            variants.add(keyword.replaceAll("en", ""));
        } else {
            if(keyword.contains("wood")) {
                variants.add(keyword.replaceAll("wood", "wooden"));
            }
            if(keyword.contains("gold")) {
                variants.add(keyword.replaceAll("gold", "golden"));
            }
        }

        // Swap words
        if(keyword.matches("\\w*[A-Z]\\w*")) {
            byte[] keywordBytes = keyword.getBytes();
            for(int i = 0; i < keywordBytes.length; i++) {
                if(keywordBytes[i] >= 'A' && keywordBytes[i] <= 'Z') {
                    String swapped = (keyword.substring(i) + keyword.substring(0, i)).toLowerCase();
                    variants.add(swapped);
                    variants.addAll(getKeywordVariants(swapped));
                }
            }
        }

        return variants;
    }
}
